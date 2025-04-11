package com.odp.walled.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.odp.walled.dto.LoginRequest;
import com.odp.walled.dto.LoginResponse;
import com.odp.walled.dto.RegisterRequest;
import com.odp.walled.dto.RegisterResponse;
import com.odp.walled.exception.DuplicateException;
import com.odp.walled.model.User;
import com.odp.walled.repository.UserRepository;
import com.odp.walled.util.JwtUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    private static JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("Email already exists");
        }

        // Encode the password
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encode password
                .fullname(request.getFullname())
                .phoneNumber(request.getPhoneNumber())
                .username(request.getUsername())
                .avatarUrl(request.getAvatarUrl())
                .build();
        userRepository.save(user);

        return new RegisterResponse(
                "User registered successfully",
                user.getId().toString(),
                user.getEmail(),
                user.getFullname(),
                user.getPhoneNumber(),
                user.getUsername(),
                user.getAvatarUrl()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DuplicateException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail(), user.getId());
        return new LoginResponse("Login successful", token);
    }
}
