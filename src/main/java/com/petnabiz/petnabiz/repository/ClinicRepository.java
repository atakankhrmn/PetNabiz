package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, String> {

    Optional<Clinic> findByClinicId(String clinicId);
    boolean existsByClinicId(String clinicId);

    Optional<Clinic> findByUser(User user);
    Optional<Clinic> findByUser_UserId(String userId);

    Optional<Clinic> findByUser_Email(String email);
    boolean existsByUser_Email(String email);

    List<Clinic> findByCityIgnoreCase(String city);
    List<Clinic> findByCityIgnoreCaseAndDistrictIgnoreCase(String city, String district);
    List<Clinic> findByNameContainingIgnoreCase(String namePart);
}
