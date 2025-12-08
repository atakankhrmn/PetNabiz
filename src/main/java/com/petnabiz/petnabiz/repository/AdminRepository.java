package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Admin;
import com.petnabiz.petnabiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    // 1) ID bazlı erişim (JpaRepository'de de var ama isim net olsun diye)
    Optional<Admin> findByAdminId(String adminId);

    boolean existsByAdminId(String userId);

    // 2) User ilişkisi üzerinden erişim
    Optional<Admin> findByUser(User user);

    Optional<Admin> findByUserUserId(String userId);

    Optional<Admin> findByUserEmail(String email);

    boolean existsByUserEmail(String email);

    // 3) İsim üzerinden arama
    Optional<Admin> findByFullName(String fullName);

    List<Admin> findByFullNameContainingIgnoreCase(String fullNamePart);
}
