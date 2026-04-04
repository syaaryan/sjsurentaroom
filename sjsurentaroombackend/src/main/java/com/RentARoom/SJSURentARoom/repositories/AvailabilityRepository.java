package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByRoom_RoomIdAndStatus(Long roomId, Availability.SlotStatus status);
}