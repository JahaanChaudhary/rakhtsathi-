package com.app.blooddonor.dto;

import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.model.User;
import org.springframework.stereotype.Component;

@Component
public class DonorMapper {

    // DonorProfile entity → DonorDTO (no password exposed)
    public DonorDTO toDTO(DonorProfile profile) {
        DonorDTO dto = new DonorDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getUser().getName());
        dto.setBloodGroup(profile.getBloodGroup());
        dto.setCity(profile.getCity());
        dto.setState(profile.getState());
        dto.setAddress(profile.getAddress());
        dto.setPhone(profile.getPhone());
        dto.setAvailable(profile.isAvailable());
        dto.setTotalDonations(profile.getTotalDonations());
        if (profile.getLastDonated() != null) {
            dto.setLastDonated(profile.getLastDonated().toString());
        }
        dto.setLatitude(profile.getLatitude());
        dto.setLongitude(profile.getLongitude());
        return dto;
    }

    // User entity → UserDTO (no password exposed)
    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
