package com.app.blooddonor.service;

import com.app.blooddonor.dto.SearchRequestDTO;
import com.app.blooddonor.model.BloodRequest;
import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.repository.BloodRequestRepository;
import com.app.blooddonor.repository.DonorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final DonorProfileRepository donorRepo;
    private final BloodRequestRepository requestRepo;
    private final EmailService           emailService;

    // ── Blood group compatibility map ──────────────────────
    private static final Map<String, List<String>> COMPATIBLE = new HashMap<>();
    static {
        COMPATIBLE.put("A+",  List.of("A+", "A-", "O+", "O-"));
        COMPATIBLE.put("A-",  List.of("A-", "O-"));
        COMPATIBLE.put("B+",  List.of("B+", "B-", "O+", "O-"));
        COMPATIBLE.put("B-",  List.of("B-", "O-"));
        COMPATIBLE.put("AB+", List.of("A+","A-","B+","B-","AB+","AB-","O+","O-"));
        COMPATIBLE.put("AB-", List.of("A-","B-","AB-","O-"));
        COMPATIBLE.put("O+",  List.of("O+", "O-"));
        COMPATIBLE.put("O-",  List.of("O-"));
    }

    // ── Main entry point — called from SearchController ────
    @Transactional
    public List<DonorProfile> searchAndNotify(SearchRequestDTO form) {
        log.info("Search — bloodGroup: {}, city: {}, requester: {}",
            form.getBloodGroup(), form.getCity(), form.getRequesterEmail());

        // Step 1 — exact match
        List<DonorProfile> donors =
            donorRepo.findByBloodGroupAndCityIgnoreCaseAndIsAvailableTrue(
                form.getBloodGroup(), form.getCity()
            );
        log.debug("Exact match: {} donors found", donors.size());

        // Step 2 — fallback: compatible blood groups
        if (donors.isEmpty()) {
            log.info("No exact match — trying compatible groups for {}",
                form.getBloodGroup());
            List<String> compatibles = COMPATIBLE.getOrDefault(
                form.getBloodGroup(), List.of()
            );
            donors = donorRepo
                .findByCityIgnoreCaseAndIsAvailableTrue(form.getCity())
                .stream()
                .filter(d -> compatibles.contains(d.getBloodGroup()))
                .toList();
            log.info("Compatible match: {} donors found", donors.size());
        }

        // Step 3 — save blood request to DB
        BloodRequest req = new BloodRequest();
        req.setRequesterName(form.getRequesterName());
        req.setRequesterEmail(form.getRequesterEmail());
        req.setRequesterPhone(form.getRequesterPhone());
        req.setBloodGroup(form.getBloodGroup());
        req.setCity(form.getCity());
        req.setHospital(form.getHospital());
        req.setMessage(form.getMessage());
        req.setDonorsNotified(donors.size());
        requestRepo.save(req);
        log.info("BloodRequest saved — id: {}, notified: {}",
            req.getId(), donors.size());

        // Step 4 — email each donor asynchronously
        for (DonorProfile donor : donors) {
            emailService.sendDonorNotification(
                donor.getUser().getEmail(),
                donor.getUser().getName(),
                form.getBloodGroup(),
                form.getCity(),
                form.getHospital(),
                form.getMessage(),
                form.getRequesterPhone()
            );
        }

        return donors;
    }
}
