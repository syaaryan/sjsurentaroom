package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // Some additional custom queries (not built into JPA)
    List<Room> findByAvailableForBookingTrue();
    List<Room> findByBuilding(String building);

    
}
