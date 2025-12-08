package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 1) ID bazlı (domain-friendly)
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);

    // 2) Email bazlı (login için KRİTİK)
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // 3) Role filtreleri (ADMIN / OWNER / VET / CLINIC)
    List<User> findByRole(String role);

    // 4) Active / passive check
    List<User> findByIsActive(boolean active);

    // 5) Login check (email + password)
    // (Bu JPA için tamamen legal çünkü modelde hem email hem password var)
    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findByIsActiveFalse();

    List<User> findByIsActiveTrue();

    List<User> findByRoleIgnoreCase(String role);
}
