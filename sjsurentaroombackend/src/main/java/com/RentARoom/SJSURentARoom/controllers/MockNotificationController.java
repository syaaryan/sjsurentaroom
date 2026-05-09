package com.RentARoom.SJSURentARoom.controllers;

import com.RentARoom.SJSURentARoom.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
public class MockNotificationController {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationController.class);

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestBody NotificationRequest request) {
        log.info("[MOCK] Sending confirmation email to {} for reservation #{} ({} {} -> {})",
                request.getRecipientEmail(),
                request.getReservationId(),
                request.getRoomName(),
                request.getStartTime(),
                request.getEndTime());
        return ResponseEntity.ok("Notification sent to " + request.getRecipientEmail());
    }
}
