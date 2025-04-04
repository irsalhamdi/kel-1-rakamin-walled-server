package com.odp.walled.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.lang.Nullable;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,32}$", message = "Password must contain uppercase, lowercase, digit, and special character (!@#$%^&*)")
    private String password;

    @NotBlank(message = "Full Name is required")
    private String fullname;

    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^0\\d{9,12}$", message = "Phone number must start with 0 and be 10 to 13 digits long")
    private String phoneNumber;

    @NotBlank(message = "Username is required")
    private String username;
    @Nullable
    private String avatarUrl;
}
