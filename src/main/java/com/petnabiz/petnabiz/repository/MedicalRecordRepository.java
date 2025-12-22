package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {

    // 1) ID bazlı erişim (JpaRepository.findById ile aynı, isim daha domain-friendly)
    Optional<MedicalRecord> findByRecordId(String recordId);


    // 2) Tarihe göre kayıtlar (rapor / geçmiş ekranları için)
    List<MedicalRecord> findByDate(LocalDate date);

    List<MedicalRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);


    // 3) PET bazlı sorgular (pet medical history)
    List<MedicalRecord> findByPet(Pet pet);

    List<MedicalRecord> findByPet_PetId(String petId);

    List<MedicalRecord> findByPet_PetIdAndDateBetween(
            String petId,
            LocalDate startDate,
            LocalDate endDate
    );


    // 4) VETERINARY bazlı sorgular (doktorun baktığı vakalar)
    List<MedicalRecord> findByVeterinary(Veterinary veterinary);

    List<MedicalRecord> findByVeterinary_VetId(String vetId);

    List<MedicalRecord> findByVeterinary_VetIdAndDateBetween(
            String vetId,
            LocalDate startDate,
            LocalDate endDate
    );


    // 5) Açıklama üzerinden arama (search / filtre)
    List<MedicalRecord> findByDescriptionContainingIgnoreCase(String text);
}
