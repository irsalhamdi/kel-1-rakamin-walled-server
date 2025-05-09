package com.odp.walled.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.odp.walled.dto.APIResponse;
import com.odp.walled.dto.LoginRequest;
import com.odp.walled.dto.LoginResponse;
import com.odp.walled.dto.RegisterRequest;
import com.odp.walled.dto.RegisterResponse;
import com.odp.walled.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse data = authService.register(request);
        APIResponse<RegisterResponse> response = new APIResponse<>("success", "User registered successfully", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse data = authService.login(request);
        APIResponse<LoginResponse> response = new APIResponse<>("success", "Login successful", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new APIResponse<>("error", "Token missing", null));
        }

        String token = authHeader.substring(7);

        try {
            authService.logout(token);
            return ResponseEntity.ok(new APIResponse<>("success", "Logout berhasil", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>("error", e.getMessage(), null));
        }
    }
}
