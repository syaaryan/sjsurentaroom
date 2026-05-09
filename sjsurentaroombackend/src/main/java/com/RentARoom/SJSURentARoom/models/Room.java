package com.RentARoom.SJSURentARoom.models;

public class Room {

    private Long roomId;
    private String name;
    private String building;
    private int capacity;
    private String amenities;
    private String description;
    private boolean availableForBooking = true;


    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailableForBooking() { return availableForBooking; }
    public void setAvailableForBooking(boolean availableForBooking) { this.availableForBooking = availableForBooking; }

}
