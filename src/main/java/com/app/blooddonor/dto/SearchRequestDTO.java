package com.app.blooddonor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

// Binds the search form fields — validated before service call
@Data
public class SearchRequestDTO {

    @NotBlank(message = "Your name is required")
    private String requesterName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String requesterEmail;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String requesterPhone;

    @NotBlank(message = "Blood group is required")
    private String bloodGroup;

    @NotBlank(message = "City is required")
    private String city;

    private String hospital;
    private String message;
}
