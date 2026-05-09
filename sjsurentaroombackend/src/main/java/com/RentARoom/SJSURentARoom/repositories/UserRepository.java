package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.User;
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
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<User> ROW_MAPPER = (rs, n) -> {
        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        String role = rs.getString("role");
        if (role != null) u.setRole(User.Role.valueOf(role));
        return u;
    };

    public List<User> findAll() {
        return jdbc.query("SELECT user_id, name, email, password, role FROM users", ROW_MAPPER);
    }

    public Optional<User> findById(Long id) {
        try {
            User u = jdbc.queryForObject(
                    "SELECT user_id, name, email, password, role FROM users WHERE user_id=?",
                    ROW_MAPPER, id);
            return Optional.ofNullable(u);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            User u = jdbc.queryForObject(
                    "SELECT user_id, name, email, password, role FROM users WHERE email=?",
                    ROW_MAPPER, email);
            return Optional.ofNullable(u);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public User save(User user) {
        if (user.getUserId() == null) {
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRole() != null ? user.getRole().name() : null);
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key != null) user.setUserId(key.longValue());
        } else {
            jdbc.update(
                    "UPDATE users SET name=?, email=?, password=?, role=? WHERE user_id=?",
                    user.getName(), user.getEmail(), user.getPassword(),
                    user.getRole() != null ? user.getRole().name() : null,
                    user.getUserId());
        }
        return user;
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM users WHERE user_id=?", id);
    }
}
