package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.Room;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepository {

    private final JdbcTemplate jdbc;

    public RoomRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    static final RowMapper<Room> ROW_MAPPER = (rs, n) -> {
        Room r = new Room();
        r.setRoomId(rs.getLong("room_id"));
        r.setName(rs.getString("name"));
        r.setBuilding(rs.getString("building"));
        r.setCapacity(rs.getInt("capacity"));
        r.setAmenities(rs.getString("amenities"));
        r.setDescription(rs.getString("description"));
        r.setAvailableForBooking(rs.getBoolean("available_for_booking"));
        return r;
    };

    private static final String SELECT_ALL =
            "SELECT room_id, name, building, capacity, amenities, description, available_for_booking FROM rooms";

    public List<Room> findAll() {
        return jdbc.query(SELECT_ALL, ROW_MAPPER);
    }

    public Optional<Room> findById(Long id) {
        try {
            Room r = jdbc.queryForObject(SELECT_ALL + " WHERE room_id=?", ROW_MAPPER, id);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Room> findByAvailableForBookingTrue() {
        return jdbc.query(SELECT_ALL + " WHERE available_for_booking = TRUE", ROW_MAPPER);
    }

    public List<Room> findByBuilding(String building) {
        return jdbc.query(SELECT_ALL + " WHERE building=?", ROW_MAPPER, building);
    }

    public Room save(Room room) {
        if (room.getRoomId() == null) {
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO rooms (name, building, capacity, amenities, description, available_for_booking) VALUES (?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, room.getName());
                ps.setString(2, room.getBuilding());
                ps.setInt(3, room.getCapacity());
                ps.setString(4, room.getAmenities());
                ps.setString(5, room.getDescription());
                ps.setBoolean(6, room.isAvailableForBooking());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key != null) room.setRoomId(key.longValue());
        } else {
            jdbc.update(
                    "UPDATE rooms SET name=?, building=?, capacity=?, amenities=?, description=?, available_for_booking=? WHERE room_id=?",
                    room.getName(), room.getBuilding(), room.getCapacity(),
                    room.getAmenities(), room.getDescription(), room.isAvailableForBooking(),
                    room.getRoomId());
        }
        return room;
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM rooms WHERE room_id=?", id);
    }
}
