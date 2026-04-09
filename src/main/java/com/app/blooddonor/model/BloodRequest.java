package com.app.blooddonor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
@Data
@NoArgsConstructor
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Your name is required")
    private String requesterName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String requesterEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$",
             message = "Phone must be exactly 10 digits")
    private String requesterPhone;

    @NotBlank(message = "Blood group is required")
    private String bloodGroup;

    @NotBlank(message = "City is required")
    private String city;

    @Size(max = 100)
    private String hospital;

    @Size(max = 500)
    private String message;

    private String status = "PENDING"; // PENDING or FULFILLED

    private int donorsNotified = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
}
