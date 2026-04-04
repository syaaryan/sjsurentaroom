package com.RentARoom.SJSURentARoom.services;

import com.RentARoom.SJSURentARoom.models.Availability;
import com.RentARoom.SJSURentARoom.models.Room;
import com.RentARoom.SJSURentARoom.repositories.AvailabilityRepository;
import com.RentARoom.SJSURentARoom.repositories.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final RoomRepository roomRepository;


    public AvailabilityService(AvailabilityRepository availabilityRepository, RoomRepository roomRepository) {
        this.availabilityRepository = availabilityRepository;
        this.roomRepository = roomRepository;
    }

    public List<Availability> getAllAvailabilities() {
        return availabilityRepository.findAll();
    }

    public List<Availability> getByRoomAndAvailabilities(Long roomId, Availability.SlotStatus availability) {
        return availabilityRepository.findByRoom_RoomIdAndStatus(roomId, availability);
    }

    public Availability createAvailability(Availability availability) {
        return availabilityRepository.save(availability);
    }

    public Availability createAvailabilityForRoom(Long roomId, Availability availability) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId));
        availability.setRoom(room);
        return availabilityRepository.save(availability);
    }

    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }
    

}