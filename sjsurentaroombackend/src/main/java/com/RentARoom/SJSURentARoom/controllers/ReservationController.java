package com.RentARoom.SJSURentARoom.controllers;

import com.RentARoom.SJSURentARoom.dto.ReservationResponse;
import com.RentARoom.SJSURentARoom.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations()
                .stream().map(ReservationResponse::from).toList();
    }

    @GetMapping("/user")
    public List<ReservationResponse> getMyReservations(Authentication authentication) {
        return reservationService.getReservationsByUser(authentication.getName())
                .stream().map(ReservationResponse::from).toList();
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookRoom(@RequestBody Map<String, Object> body, Authentication authentication) {
        try {
            String email  = authentication.getName();
            Long roomId   = Long.valueOf(body.get("roomId").toString());
            Long slotId   = Long.valueOf(body.get("slotId").toString());
            String notes  = body.getOrDefault("notes", "").toString();

            return ResponseEntity.ok(ReservationResponse.from(
                    reservationService.bookRoom(email, roomId, slotId, notes)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
