package com.app.blooddonor.dto;

import lombok.Data;

// Sent to frontend instead of raw User entity
// Does NOT contain password field — safe to expose
@Data
public class UserDTO {
    private Long   id;
    private String name;
    private String email;
    private String role;
}
