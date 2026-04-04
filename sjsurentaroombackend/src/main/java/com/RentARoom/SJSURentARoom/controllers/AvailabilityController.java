package com.RentARoom.SJSURentARoom.controllers;

import com.RentARoom.SJSURentARoom.models.Availability;
import com.RentARoom.SJSURentARoom.services.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public List<Availability> getAllAvailabilities() {
        return availabilityService.getAllAvailabilities();
    }

    @GetMapping("/room/{roomId}")
    public List<Availability> getByRoom(@PathVariable Long roomId,
                                        @RequestParam(defaultValue = "OPEN") Availability.SlotStatus status) {
        return availabilityService.getByRoomAndAvailabilities(roomId, status);
    }

    @PostMapping
    public Availability createAvailability(@Valid @RequestBody Availability availability) {
        return availabilityService.createAvailability(availability);
    }

    @PostMapping("/room/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Availability createAvailabilityForRoom(@PathVariable Long roomId,
                                                  @Valid @RequestBody Availability availability) {
        return availabilityService.createAvailabilityForRoom(roomId, availability);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
