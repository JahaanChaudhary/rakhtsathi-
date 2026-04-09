package com.app.blooddonor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donor_profiles")
@Data
@NoArgsConstructor
public class DonorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$",
             message = "Invalid blood group. Must be A+, A-, B+, B-, O+, O-, AB+ or AB-")
    private String bloodGroup;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$",
             message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Size(max = 200)
    private String address;

    // Map coordinates — populated by DataSeeder
    private Double latitude;
    private Double longitude;

    private boolean isAvailable = true;

    @PastOrPresent(message = "Last donated date cannot be in the future")
    private LocalDate lastDonated;

    @Min(value = 0, message = "Total donations cannot be negative")
    private int totalDonations = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
}
