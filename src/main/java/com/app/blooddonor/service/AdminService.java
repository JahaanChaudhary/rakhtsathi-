package com.app.blooddonor.service;

import com.app.blooddonor.dto.DonorDTO;
import com.app.blooddonor.dto.DonorMapper;
import com.app.blooddonor.exception.ResourceNotFoundException;
import com.app.blooddonor.model.BloodRequest;
import com.app.blooddonor.repository.BloodRequestRepository;
import com.app.blooddonor.repository.DonorProfileRepository;
import com.app.blooddonor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository         userRepo;
    private final DonorProfileRepository donorRepo;
    private final BloodRequestRepository requestRepo;
    private final DonorMapper            donorMapper;

    @Transactional(readOnly = true)
    public long getTotalUsers()      { return userRepo.count(); }
    @Transactional(readOnly = true)
    public long getTotalDonors()     { return donorRepo.count(); }
    @Transactional(readOnly = true)
    public long getAvailableDonors() { return donorRepo.countByIsAvailableTrue(); }
    @Transactional(readOnly = true)
    public long getTotalRequests()   { return requestRepo.count(); }
    @Transactional(readOnly = true)
    public long getPendingRequests() { return requestRepo.countByStatus("PENDING"); }

    // Returns ALL requests — JS handles pagination client-side (no reload)
    @Transactional(readOnly = true)
    public List<BloodRequest> getAllRequests() {
        return requestRepo.findAllByOrderByCreatedAtDesc();
    }

    // Returns ALL donors as DTOs — JS handles pagination client-side (no reload)
    @Transactional(readOnly = true)
    public List<DonorDTO> getAllDonors() {
        return donorRepo.findAllByOrderByCreatedAtDesc(Pageable.unpaged())
                .stream()
                .map(donorMapper::toDTO)
                .toList();
    }

    @Transactional
    public void fulfillRequest(Long id) {
        BloodRequest req = requestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + id));
        if ("FULFILLED".equals(req.getStatus()))
            throw new IllegalStateException("Already fulfilled");
        req.setStatus("FULFILLED");
        requestRepo.save(req);
        log.info("Request {} marked FULFILLED", id);
    }

    @Transactional
    public void deleteDonor(Long id) {
        if (!donorRepo.existsById(id))
            throw new ResourceNotFoundException("Donor not found: " + id);
        donorRepo.deleteById(id);
        log.info("Donor {} deleted", id);
    }
}
