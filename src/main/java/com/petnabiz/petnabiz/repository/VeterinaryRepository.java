package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinaryRepository extends JpaRepository<Veterinary, String> {

    // 1) ID bazlı erişim
    Optional<Veterinary> findByVetId(String vetId);
    boolean existsByVetId(String vetId);


    // 2) İsim bazlı aramalar
    List<Veterinary> findByFirstNameIgnoreCase(String firstName);
    List<Veterinary> findByLastNameIgnoreCase(String lastName);

    List<Veterinary> findByFirstNameContainingIgnoreCase(String namePart);
    List<Veterinary> findByLastNameContainingIgnoreCase(String namePart);

    // Ad + soyad birlikte
    List<Veterinary> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);


    // 3) Telefon bazlı
    Optional<Veterinary> findByPhoneNumber(String phoneNumber);


    // 4) Adres bazlı arama
    List<Veterinary> findByAddressContainingIgnoreCase(String addressPart);


    // 5) Sertifika / diploma bazlı filtre
    List<Veterinary> findByCertificateContainingIgnoreCase(String certificatePart);


    // 6) Klinik bazlı sorgular
    List<Veterinary> findByClinic(Clinic clinic);

    List<Veterinary> findByClinic_ClinicId(String clinicId);
}
