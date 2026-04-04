package com.RentARoom.SJSURentARoom.dto;

import com.RentARoom.SJSURentARoom.models.Reservation;

public class ReservationResponse {

    private Long reservationId;
    private Long roomId;
    private String roomName;
    private String building;
    private Long slotId;
    private String startTime;
    private String endTime;
    private String status;
    private String notes;

    private Integer capacity;

    public static ReservationResponse from(Reservation r) {
        ReservationResponse dto = new ReservationResponse();
        dto.reservationId = r.getReservationId();
        dto.roomId       = r.getRoom().getRoomId();
        dto.roomName     = r.getRoom().getName();
        dto.building     = r.getRoom().getBuilding();
        dto.capacity     = r.getRoom().getCapacity();
        dto.slotId       = r.getSlot().getSlotId();
        dto.startTime    = r.getSlot().getStartTime().toString();
        dto.endTime      = r.getSlot().getEndTime().toString();
        dto.status       = r.getStatus().name();
        dto.notes        = r.getNotes();
        return dto;
    }

    public Long getReservationId()  { return reservationId; }
    public Long getRoomId()         { return roomId; }
    public String getRoomName()     { return roomName; }
    public String getBuilding()     { return building; }
    public Integer getCapacity()    { return capacity; }
    public Long getSlotId()         { return slotId; }
    public String getStartTime()    { return startTime; }
    public String getEndTime()      { return endTime; }
    public String getStatus()       { return status; }
    public String getNotes()        { return notes; }
}
