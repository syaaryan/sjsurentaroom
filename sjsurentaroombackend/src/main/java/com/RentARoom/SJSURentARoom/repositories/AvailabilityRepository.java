package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.Availability;
import com.RentARoom.SJSURentARoom.models.Room;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class AvailabilityRepository {

    private final JdbcTemplate jdbc;

    public AvailabilityRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT_JOIN =
            "SELECT a.slot_id, a.room_id, a.start_time, a.end_time, a.status, a.version, " +
            "r.name AS r_name, r.building AS r_building, r.capacity AS r_capacity, " +
            "r.amenities AS r_amenities, r.description AS r_description, " +
            "r.available_for_booking AS r_available_for_booking " +
            "FROM availability a JOIN rooms r ON a.room_id = r.room_id";

    private static final RowMapper<Availability> JOIN_MAPPER = (rs, n) -> {
        Availability a = new Availability();
        a.setSlotId(rs.getLong("slot_id"));
        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        a.setStartTime(st != null ? st.toLocalDateTime() : null);
        a.setEndTime(et != null ? et.toLocalDateTime() : null);
        String status = rs.getString("status");
        if (status != null) a.setStatus(Availability.SlotStatus.valueOf(status));
        long ver = rs.getLong("version");
        a.setVersion(rs.wasNull() ? 0L : ver);

        Room r = new Room();
        r.setRoomId(rs.getLong("room_id"));
        r.setName(rs.getString("r_name"));
        r.setBuilding(rs.getString("r_building"));
        r.setCapacity(rs.getInt("r_capacity"));
        r.setAmenities(rs.getString("r_amenities"));
        r.setDescription(rs.getString("r_description"));
        r.setAvailableForBooking(rs.getBoolean("r_available_for_booking"));
        a.setRoom(r);
        return a;
    };

    public List<Availability> findAll() {
        return jdbc.query(SELECT_JOIN, JOIN_MAPPER);
    }

    public Optional<Availability> findById(Long id) {
        try {
            Availability a = jdbc.queryForObject(
                    SELECT_JOIN + " WHERE a.slot_id=?", JOIN_MAPPER, id);
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Availability> findByRoomIdAndStatus(Long roomId, Availability.SlotStatus status) {
        return jdbc.query(
                SELECT_JOIN + " WHERE a.room_id=? AND a.status=?",
                JOIN_MAPPER, roomId, status.name());
    }

    public Availability save(Availability a) {
        if (a.getSlotId() == null) {
            if (a.getVersion() == null) a.setVersion(0L);
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO availability (room_id, start_time, end_time, status, version) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, a.getRoom().getRoomId());
                ps.setTimestamp(2, Timestamp.valueOf(a.getStartTime()));
                ps.setTimestamp(3, Timestamp.valueOf(a.getEndTime()));
                ps.setString(4, a.getStatus().name());
                ps.setLong(5, a.getVersion());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key != null) a.setSlotId(key.longValue());
            return a;
        } else {
            jdbc.update(
                    "UPDATE availability SET status=?, version=version+1 WHERE slot_id=?",
                    a.getStatus().name(), a.getSlotId());
            a.setVersion(a.getVersion() == null ? 1L : a.getVersion() + 1);
            return a;
        }
    }

    public boolean updateStatusWithVersion(Long slotId, Availability.SlotStatus newStatus, Long expectedVersion) {
        int rows = jdbc.update(
                "UPDATE availability SET status=?, version=COALESCE(version,0)+1 " +
                "WHERE slot_id=? AND COALESCE(version,0)=?",
                newStatus.name(), slotId, expectedVersion);
        return rows == 1;
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM availability WHERE slot_id=?", id);
    }
}
