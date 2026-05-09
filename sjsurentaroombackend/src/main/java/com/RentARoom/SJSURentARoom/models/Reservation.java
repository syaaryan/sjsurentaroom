package com.RentARoom.SJSURentARoom.models;

public class Reservation {

    private Long reservationId;
    private User user;
    private Room room;
    private Availability slot;
    private ReservationStatus status = ReservationStatus.CONFIRMED;
    private String notes;

    public enum ReservationStatus { CONFIRMED, CANCELLED, RESCHEDULED }


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
