package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_UserId(Long userId);
    List<Reservation> findByRoom_RoomId(Long roomId);
}