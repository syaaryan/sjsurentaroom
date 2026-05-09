package com.RentARoom.SJSURentARoom.dto;

public class NotificationRequest {

    private String recipientEmail;
    private String recipientName;
    private String roomName;
    private String building;
    private String startTime;
    private String endTime;
    private Long reservationId;

    public NotificationRequest() {}

    public NotificationRequest(String recipientEmail, String recipientName,
                               String roomName, String building,
                               String startTime, String endTime,
                               Long reservationId) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.roomName = roomName;
        this.building = building;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationId = reservationId;
    }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
}
