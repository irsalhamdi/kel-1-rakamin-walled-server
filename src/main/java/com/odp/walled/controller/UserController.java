package com.odp.walled.controller;

import com.odp.walled.dto.APIResponse;
import com.odp.walled.dto.BaseResponse;
import com.odp.walled.dto.UserRequest;
import com.odp.walled.dto.UserResponse;
import com.odp.walled.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public ResponseEntity<APIResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse data = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>("success", "User created successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse data = userService.getUserById(id);
        return ResponseEntity.ok(new APIResponse<>("success", "User retrieved successfully", data));
    }

    @GetMapping("/me")
    public ResponseEntity<APIResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>("error", "Unauthorized", null));
        }

        UserResponse data = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(new APIResponse<>("success", "User profile retrieved", data));
    }
}