package com.RentARoom.SJSURentARoom.models;

import java.time.LocalDateTime;

public class Availability {

    private Long slotId;
    private Room room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SlotStatus status;
    private Long version;

    public enum SlotStatus { OPEN, BOOKED, BLOCKED }


    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

}
