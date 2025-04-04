package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private String userId;
    private String email;
    private String fullname;
    private String phoneNumber;
    private String username;
    private String avatarUrl;
}