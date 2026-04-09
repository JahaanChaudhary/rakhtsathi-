package com.app.blooddonor.repository;

import com.app.blooddonor.model.BloodRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    // Non-paged — kept for compatibility
    List<BloodRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    // Paginated — used by AdminService
    Page<BloodRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
