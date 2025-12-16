package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.ApplicationStatus;
import com.petnabiz.petnabiz.model.ClinicApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicApplicationRepository extends JpaRepository<ClinicApplication, Long> {

    boolean existsByEmail(String email);

    List<ClinicApplication> findByStatus(ApplicationStatus status);
}
