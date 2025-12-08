package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Medication;
import com.petnabiz.petnabiz.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, String> {

    // 1) ID bazlı erişim (okunabilirlik için)
    Optional<Medication> findByMedicationId(String medicationId);


    // 2) İlaç (Medicine) bazlı sorgular

    // Elinde Medicine entity varsa:
    List<Medication> findByMedicine(Medicine medicine);

    // Sadece medicineId biliyorsan:
    List<Medication> findByMedicine_MedicineId(String medicineId);

    // İlaç ismine göre arama (ör: "Parol", "Antibiotic")
    List<Medication> findByMedicine_NameContainingIgnoreCase(String namePart);

    // İlaç tipine göre (ör: "tablet", "injection")
    List<Medication> findByMedicine_TypeIgnoreCase(String type);


    // 3) Tarih bazlı sorgular

    // Belli bir tarih aralığındaki reçeteler:
    List<Medication> findByStartGreaterThanEqualAndEndLessThanEqual(
            LocalDate start,
            LocalDate end
    );

    // Belirli bir tarihte aktif olan ilaçlar:
    // Bu methodu çağırırken date'i iki parametreye de aynı verirsin:
    // repo.findByStartLessThanEqualAndEndGreaterThanEqual(date, date);
    List<Medication> findByStartLessThanEqualAndEndGreaterThanEqual(
            LocalDate start,
            LocalDate end
    );


    // 4) Talimat (instructions) bazlı arama
    List<Medication> findByInstructionsContainingIgnoreCase(String text);

    List<Medication> findByMedicalRecord_RecordId(String recordId);

    List<Medication> findByPet_PetId(String petId);
}
