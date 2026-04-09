package com.app.blooddonor.service;

import com.app.blooddonor.exception.DonorProfileNotFoundException;
import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.model.User;
import com.app.blooddonor.repository.DonorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonorService {

    private final DonorProfileRepository donorRepo;
    private final EmailService           emailService;

    // ── Create new profile ─────────────────────────────────
    @Transactional
    public DonorProfile registerProfile(User user, DonorProfile profile) {
        log.info("Creating donor profile for: {}", user.getEmail());

        if (donorRepo.findByUser(user).isPresent()) {
            throw new IllegalStateException(
                "Donor profile already exists for: " + user.getEmail()
            );
        }

        profile.setUser(user);
        DonorProfile saved = donorRepo.save(profile);
        log.info("Donor profile created — id: {}, bloodGroup: {}, city: {}",
            saved.getId(), saved.getBloodGroup(), saved.getCity());

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        return saved;
    }

    // ── Update existing profile ────────────────────────────
    @Transactional
    public DonorProfile updateProfile(User user, DonorProfile updated) {
        log.info("Updating donor profile for: {}", user.getEmail());

        DonorProfile existing = donorRepo.findByUser(user)
            .orElseThrow(() -> new DonorProfileNotFoundException(
                "No donor profile found for: " + user.getEmail()
            ));

        existing.setBloodGroup(updated.getBloodGroup());
        existing.setPhone(updated.getPhone());
        existing.setCity(updated.getCity());
        existing.setState(updated.getState());
        existing.setAddress(updated.getAddress());
        existing.setLastDonated(updated.getLastDonated());
        existing.setTotalDonations(updated.getTotalDonations());

        DonorProfile saved = donorRepo.save(existing);
        log.info("Profile updated — id: {}", saved.getId());
        return saved;
    }

    // ── Find profile by user ───────────────────────────────
    @Transactional(readOnly = true)
    public Optional<DonorProfile> findByUser(User user) {
        return donorRepo.findByUser(user);
    }

    // ── Toggle availability ────────────────────────────────
    @Transactional
    public void toggleAvailability(User user) {
        log.info("Toggling availability for: {}", user.getEmail());

        DonorProfile profile = donorRepo.findByUser(user)
            .orElseThrow(() -> new DonorProfileNotFoundException(
                "No donor profile found for: " + user.getEmail()
            ));

        boolean prev = profile.isAvailable();
        profile.setAvailable(!prev);
        donorRepo.save(profile);

        log.info("Availability toggled: {} → {}", prev, !prev);
    }

    // ── Save — used by DataSeeder only ─────────────────────
    @Transactional
    public DonorProfile save(DonorProfile profile) {
        return donorRepo.save(profile);
    }

    public long countAvailable() {
        return donorRepo.countByIsAvailableTrue();
    }
}
