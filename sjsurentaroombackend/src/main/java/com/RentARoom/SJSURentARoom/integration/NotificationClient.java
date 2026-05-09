package com.RentARoom.SJSURentARoom.integration;

import com.RentARoom.SJSURentARoom.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:${server.port:10000}}")
    private String notificationServiceUrl;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendConfirmation(NotificationRequest request) {
        String url = notificationServiceUrl + "/notify/confirm";
        log.info("Calling external notification service at {} for reservationId={}",
                url, request.getReservationId());
        restTemplate.postForEntity(url, request, String.class);
    }
}
