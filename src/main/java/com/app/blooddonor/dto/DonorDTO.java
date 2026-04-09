package com.app.blooddonor.dto;

import lombok.Data;

@Data
public class DonorDTO {
    private Long    id;
    private String  name;
    private String  bloodGroup;
    private String  city;
    private String  state;
    private String  address;
    private String  phone;
    private boolean available;
    private int     totalDonations;
    private String  lastDonated;
    private Double  latitude;
    private Double  longitude;

    // Set by SearchController after mapping.
    // Shows "Within 2 km", "Within 5 km", "Same city" etc.
    // Coordinates are NEVER sent to the frontend — only this string.
    private String distanceRange;
}
