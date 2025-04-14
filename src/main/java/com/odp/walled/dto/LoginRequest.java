package com.odp.walled.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
    private String email;
    @NotEmpty(message = "Password is required")
    private String password;
}
