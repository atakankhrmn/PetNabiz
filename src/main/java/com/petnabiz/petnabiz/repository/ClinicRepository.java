package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, String> {

    // 1) ID bazlı erişim
    // JpaRepository.findById(clinicId) zaten var ama domain ismiyle kullanmak okunaklı
    Optional<Clinic> findByClinicId(String clinicId);

    boolean existsByClinicId(String clinicId);


    // 2) User ilişkisi üzerinden erişim
    // Clinic <-> User one-to-one (clinicId = userId)

    // Elinde direkt User nesnesi varsa:
    Optional<Clinic> findByUser(User user);

    Optional<Clinic> findByEmail(String email);
    boolean existsByEmail(String email);

    // Elinde sadece userId varsa:
    Optional<Clinic> findByUser_UserId(String userId);

    // Login / email bazlı bulmak istersen:
    Optional<Clinic> findByUser_Email(String email);

    boolean existsByUser_Email(String email);


    // 3) Konum ve isim bazlı aramalar
    // Şehir bazlı klinik listesi
    List<Clinic> findByCityIgnoreCase(String city);

    // Şehir + ilçe bazlı filtre
    List<Clinic> findByCityIgnoreCaseAndDistrictIgnoreCase(String city, String district);

    // Klinik adında geçen kelimeye göre arama (örn: "vet", "animal" vs.)
    List<Clinic> findByNameContainingIgnoreCase(String namePart);


}
