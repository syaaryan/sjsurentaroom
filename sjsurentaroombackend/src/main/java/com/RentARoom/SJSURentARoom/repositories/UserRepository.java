package com.RentARoom.SJSURentARoom.repositories;

import com.RentARoom.SJSURentARoom.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// SQL Queries
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
