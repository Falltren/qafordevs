package com.fallt.qafordevs.repository;

import com.fallt.qafordevs.entity.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeveloperRepository extends JpaRepository<DeveloperEntity, Integer> {

    Optional<DeveloperEntity> findByEmail(String email);

    @Query("SELECT d FROM DeveloperEntity d WHERE d.status = 'ACTIVE' AND d.speciality = ?1")
    List<DeveloperEntity> findAllActiveBySpeciality(String speciality);
}
