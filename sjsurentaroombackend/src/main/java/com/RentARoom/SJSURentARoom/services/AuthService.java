package com.RentARoom.SJSURentARoom.services;

import com.RentARoom.SJSURentARoom.dto.AuthResponse;
import com.RentARoom.SJSURentARoom.dto.LoginRequest;
import com.RentARoom.SJSURentARoom.dto.RegisterRequest;
import com.RentARoom.SJSURentARoom.models.User;
import com.RentARoom.SJSURentARoom.repositories.UserRepository;
import com.RentARoom.SJSURentARoom.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration rejected: email already in use ({})", request.getEmail());
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setName(request.getFirstName() + " " + request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.STUDENT);

        userRepository.save(user);
        log.info("User registered: email={}, userId={}", user.getEmail(), user.getUserId());

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUserId(), user.getName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (RuntimeException e) {
            log.warn("Failed login attempt for email={}", request.getEmail());
            throw e;
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Login successful: email={}", user.getEmail());
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUserId(), user.getName(), user.getRole().name());
    }
}
