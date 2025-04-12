package com.odp.walled.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.odp.walled.dto.LoginRequest;
import com.odp.walled.dto.LoginResponse;
import com.odp.walled.dto.RegisterRequest;
import com.odp.walled.dto.RegisterResponse;
import com.odp.walled.exception.DuplicateException;
import com.odp.walled.exception.ResourceNotFound;
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

    private final JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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
                .orElseThrow(() -> new ResourceNotFound("Email Or Password Wrond"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email Or Password Wrond");
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponse(token);
    }
}
