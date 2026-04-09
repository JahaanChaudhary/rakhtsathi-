package com.app.blooddonor.repository;

import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {
    Optional<DonorProfile> findByUser(User user);
    List<DonorProfile> findByBloodGroupAndCityIgnoreCaseAndIsAvailableTrue(String bloodGroup, String city);
    List<DonorProfile> findByCityIgnoreCaseAndIsAvailableTrue(String city);
    long countByIsAvailableTrue();
    // Supports both Pageable.unpaged() and PageRequest.of(n,size)
    Page<DonorProfile> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
