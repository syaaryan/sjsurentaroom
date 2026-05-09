package com.RentARoom.SJSURentARoom.services;

import com.RentARoom.SJSURentARoom.models.Room;
import com.RentARoom.SJSURentARoom.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailableForBookingTrue();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updated) {
        return roomRepository.findById(id).map(room -> {
            room.setName(updated.getName());
            room.setBuilding(updated.getBuilding());
            room.setCapacity(updated.getCapacity());
            room.setAmenities(updated.getAmenities());
            room.setDescription(updated.getDescription());
            room.setAvailableForBooking(updated.isAvailableForBooking());
            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }


}