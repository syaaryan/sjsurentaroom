package com.RentARoom.SJSURentARoom.models;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private Availability slot;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    public enum ReservationStatus { CONFIRMED, CANCELLED, RESCHEDULED }

    private String notes;




    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long id) { this.reservationId = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
    public Availability getSlot() { return slot; }
    public void setSlot(Availability slot) { this.slot = slot; }
    
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
