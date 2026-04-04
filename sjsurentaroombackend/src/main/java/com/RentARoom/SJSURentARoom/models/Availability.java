package com.RentARoom.SJSURentARoom.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability")

public class Availability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SlotStatus status;

    // Optimistic locking: Hibernate increments this on every update.
    // If two transactions read the same version and both try to write,
    // the second commit will fail with ObjectOptimisticLockingFailureException
    // because the version in the DB no longer matches what was read!
    @Version
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

}
