package com.app.blooddonor.service;

import com.app.blooddonor.repository.BloodRequestRepository;
import com.app.blooddonor.repository.DonorProfileRepository;
import com.app.blooddonor.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository         userRepo;
    private final DonorProfileRepository donorRepo;
    private final BloodRequestRepository requestRepo;

    @Transactional(readOnly = true)
    public HomeStats getHomeStats() {
        HomeStats stats = new HomeStats();
        stats.setTotalDonors(userRepo.count());
        stats.setAvailableDonors(donorRepo.countByIsAvailableTrue());
        stats.setTotalRequests(requestRepo.count());
        log.debug("Stats — donors: {}, available: {}, requests: {}",
            stats.getTotalDonors(), stats.getAvailableDonors(), stats.getTotalRequests());
        return stats;
    }

    @Data
    public static class HomeStats {
        private long totalDonors;
        private long availableDonors;
        private long totalRequests;
    }
}
