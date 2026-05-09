package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.Availability;
import com.RentARoom.SJSURentARoom.models.Reservation;
import com.RentARoom.SJSURentARoom.models.Room;
import com.RentARoom.SJSURentARoom.models.User;
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
public class ReservationRepository {

    private final JdbcTemplate jdbc;

    public ReservationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT_JOIN =
            "SELECT res.reservation_id, res.user_id, res.room_id, res.slot_id, res.status, res.notes, " +
            "u.name AS u_name, u.email AS u_email, u.role AS u_role, " +
            "r.name AS r_name, r.building AS r_building, r.capacity AS r_capacity, " +
            "r.amenities AS r_amenities, r.description AS r_description, " +
            "r.available_for_booking AS r_available_for_booking, " +
            "a.start_time AS a_start_time, a.end_time AS a_end_time, " +
            "a.status AS a_status, a.version AS a_version " +
            "FROM reservation res " +
            "JOIN users u ON res.user_id = u.user_id " +
            "JOIN rooms r ON res.room_id = r.room_id " +
            "JOIN availability a ON res.slot_id = a.slot_id";

    private static final RowMapper<Reservation> JOIN_MAPPER = (rs, n) -> {
        Reservation res = new Reservation();
        res.setReservationId(rs.getLong("reservation_id"));
        String status = rs.getString("status");
        if (status != null) res.setStatus(Reservation.ReservationStatus.valueOf(status));
        res.setNotes(rs.getString("notes"));

        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setName(rs.getString("u_name"));
        u.setEmail(rs.getString("u_email"));
        String role = rs.getString("u_role");
        if (role != null) u.setRole(User.Role.valueOf(role));
        res.setUser(u);

        Room r = new Room();
        r.setRoomId(rs.getLong("room_id"));
        r.setName(rs.getString("r_name"));
        r.setBuilding(rs.getString("r_building"));
        r.setCapacity(rs.getInt("r_capacity"));
        r.setAmenities(rs.getString("r_amenities"));
        r.setDescription(rs.getString("r_description"));
        r.setAvailableForBooking(rs.getBoolean("r_available_for_booking"));
        res.setRoom(r);

        Availability a = new Availability();
        a.setSlotId(rs.getLong("slot_id"));
        a.setRoom(r);
        Timestamp st = rs.getTimestamp("a_start_time");
        Timestamp et = rs.getTimestamp("a_end_time");
        a.setStartTime(st != null ? st.toLocalDateTime() : null);
        a.setEndTime(et != null ? et.toLocalDateTime() : null);
        String astatus = rs.getString("a_status");
        if (astatus != null) a.setStatus(Availability.SlotStatus.valueOf(astatus));
        long ver = rs.getLong("a_version");
        a.setVersion(rs.wasNull() ? 0L : ver);
        res.setSlot(a);

        return res;
    };

    public List<Reservation> findAll() {
        return jdbc.query(SELECT_JOIN, JOIN_MAPPER);
    }

    public Optional<Reservation> findById(Long id) {
        try {
            Reservation r = jdbc.queryForObject(
                    SELECT_JOIN + " WHERE res.reservation_id=?", JOIN_MAPPER, id);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Reservation> findByUserId(Long userId) {
        return jdbc.query(SELECT_JOIN + " WHERE res.user_id=?", JOIN_MAPPER, userId);
    }

    public List<Reservation> findByRoomId(Long roomId) {
        return jdbc.query(SELECT_JOIN + " WHERE res.room_id=?", JOIN_MAPPER, roomId);
    }

    public Reservation save(Reservation r) {
        if (r.getReservationId() == null) {
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO reservation (user_id, room_id, slot_id, status, notes) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, r.getUser().getUserId());
                ps.setLong(2, r.getRoom().getRoomId());
                ps.setLong(3, r.getSlot().getSlotId());
                ps.setString(4, r.getStatus().name());
                ps.setString(5, r.getNotes());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key != null) r.setReservationId(key.longValue());
        } else {
            jdbc.update(
                    "UPDATE reservation SET status=?, notes=? WHERE reservation_id=?",
                    r.getStatus().name(), r.getNotes(), r.getReservationId());
        }
        return r;
    }

    public void delete(Reservation r) {
        deleteById(r.getReservationId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM reservation WHERE reservation_id=?", id);
    }
}
