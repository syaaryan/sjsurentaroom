package com.RentARoom.SJSURentARoom.services;

import com.RentARoom.SJSURentARoom.dto.NotificationRequest;
import com.RentARoom.SJSURentARoom.integration.NotificationClient;
import com.RentARoom.SJSURentARoom.models.*;
import com.RentARoom.SJSURentARoom.repositories.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final NotificationClient notificationClient;
    private final Counter bookingSuccessCounter;
    private final Counter bookingFailureCounter;

    @Lazy
    @Autowired
    private ReservationService self;

    public ReservationService(ReservationRepository reservationRepository,
                              AvailabilityRepository availabilityRepository,
                              UserRepository userRepository,
                              RoomRepository roomRepository,
                              NotificationClient notificationClient,
                              MeterRegistry registry) {
        this.reservationRepository = reservationRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.notificationClient = notificationClient;
        this.bookingSuccessCounter = registry.counter("reservations.bookings", "result", "success");
        this.bookingFailureCounter = registry.counter("reservations.bookings", "result", "failure");
    }

    private static class OptimisticConflictException extends RuntimeException {}

    public Reservation bookRoom(String email, Long roomId, Long slotId, String notes) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("Booking attempt {}/{}: user={}, roomId={}, slotId={}",
                    attempt, maxAttempts, email, roomId, slotId);
            try {
                Reservation saved = self.attemptBookRoom(email, roomId, slotId, notes);
                bookingSuccessCounter.increment();
                log.info("Reservation confirmed: reservationId={}, user={}, roomId={}, slotId={}",
                        saved.getReservationId(), email, roomId, slotId);

                try {
                    NotificationRequest notif = new NotificationRequest(
                            saved.getUser().getEmail(),
                            saved.getUser().getName(),
                            saved.getRoom().getName(),
                            saved.getRoom().getBuilding(),
                            saved.getSlot().getStartTime().toString(),
                            saved.getSlot().getEndTime().toString(),
                            saved.getReservationId()
                    );
                    notificationClient.sendConfirmation(notif);
                } catch (RuntimeException notifyEx) {
                    log.warn("Notification dispatch failed for reservationId={}: {}",
                            saved.getReservationId(), notifyEx.getMessage());
                }

                return saved;
            } catch (OptimisticConflictException e) {
                log.warn("Concurrent conflict on slotId={}, attempt={}/{}. Retrying...",
                        slotId, attempt, maxAttempts);
                if (attempt == maxAttempts) {
                    bookingFailureCounter.increment();
                    log.error("Booking failed after {} attempts for slotId={}, user={}",
                            maxAttempts, slotId, email);
                    throw new RuntimeException("Slot " + slotId + " is being booked simultaneously. Please try again.");
                }
            } catch (RuntimeException e) {
                bookingFailureCounter.increment();
                throw e;
            }
        }
        bookingFailureCounter.increment();
        throw new RuntimeException("Booking failed after " + maxAttempts + " attempts.");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Reservation attemptBookRoom(String email, Long roomId, Long slotId, String notes) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Availability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() != Availability.SlotStatus.OPEN) {
            throw new RuntimeException("Slot is not available!");
        }

        boolean updated = availabilityRepository.updateStatusWithVersion(
                slot.getSlotId(), Availability.SlotStatus.BOOKED, slot.getVersion());
        if (!updated) {
            throw new OptimisticConflictException();
        }
        slot.setStatus(Availability.SlotStatus.BOOKED);
        slot.setVersion(slot.getVersion() + 1);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setSlot(slot);
        reservation.setNotes(notes);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByUserId(user.getUserId());
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Availability slot = reservation.getSlot();
        slot.setStatus(Availability.SlotStatus.OPEN);
        availabilityRepository.save(slot);

        reservationRepository.delete(reservation);
        log.info("Reservation cancelled: reservationId={}", reservationId);
    }
}
