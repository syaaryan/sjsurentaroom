package com.RentARoom.SJSURentARoom.services;

import com.RentARoom.SJSURentARoom.models.*;
import com.RentARoom.SJSURentARoom.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Lazy
    @Autowired
    private ReservationService self;

    public ReservationService(ReservationRepository reservationRepository, AvailabilityRepository availabilityRepository, UserRepository userRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation bookRoom(String email, Long roomId, Long slotId, String notes) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return self.attemptBookRoom(email, roomId, slotId, notes);
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Slot " + slotId + " is being booked simultaneously. Please try again.");
                }
            }
        }
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

        slot.setStatus(Availability.SlotStatus.BOOKED);
        availabilityRepository.save(slot);

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
        return reservationRepository.findByUser_UserId(user.getUserId());
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.getSlot().setStatus(Availability.SlotStatus.OPEN);
        availabilityRepository.save(reservation.getSlot());

        reservationRepository.delete(reservation);
    }
}
