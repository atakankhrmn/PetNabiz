package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Medication;
import com.petnabiz.petnabiz.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> { // PK neyse ona göre

    Optional<Medication> findByMedicationId(String medicationId);

    List<Medication> findByMedicine_MedicineId(String medicineId);
    List<Medication> findByMedicine_NameContainingIgnoreCase(String namePart);
    List<Medication> findByMedicine_TypeIgnoreCase(String type);

    List<Medication> findByStartGreaterThanEqualAndEndLessThanEqual(
            LocalDate start,
            LocalDate end
    );

    List<Medication> findByStartLessThanEqualAndEndGreaterThanEqual(
            LocalDate start,
            LocalDate end
    );

    List<Medication> findByInstructionsContainingIgnoreCase(String text);

    List<Medication> findByMedicalRecord_RecordId(String recordId);
    List<Medication> findByMedicalRecord_Pet_PetId(String petId);
}

