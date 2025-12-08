package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Medication;
import com.petnabiz.petnabiz.model.Medicine;
import com.petnabiz.petnabiz.repository.MedicationRepository;
import com.petnabiz.petnabiz.repository.MedicineRepository;
import com.petnabiz.petnabiz.service.MedicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicineRepository medicineRepository;

    public MedicationServiceImpl(MedicationRepository medicationRepository,
                                 MedicineRepository medicineRepository) {
        this.medicationRepository = medicationRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }

    @Override
    public Optional<Medication> getMedicationById(String medicationId) {
        return medicationRepository.findByMedicationId(medicationId);
        // veya: return medicationRepository.findById(medicationId);
    }

    @Override
    public List<Medication> getMedicationsByMedicineId(String medicineId) {
        return medicationRepository.findByMedicine_MedicineId(medicineId);
    }

    @Override
    public List<Medication> searchByMedicineName(String namePart) {
        return medicationRepository.findByMedicine_NameContainingIgnoreCase(namePart);
    }

    @Override
    public List<Medication> getMedicationsByMedicineType(String type) {
        return medicationRepository.findByMedicine_TypeIgnoreCase(type);
    }

    @Override
    public List<Medication> getActiveMedicationsOn(LocalDate date) {
        // start <= date AND end >= date
        return medicationRepository.findByStartLessThanEqualAndEndGreaterThanEqual(date, date);
    }

    @Override
    public List<Medication> getMedicationsBetween(LocalDate start, LocalDate end) {
        // start >= given start AND end <= given end
        return medicationRepository.findByStartGreaterThanEqualAndEndLessThanEqual(start, end);
    }

    @Override
    public Medication createMedication(Medication medication) {
        /*
         * Beklediğimiz:
         *  - medication.getMedicine().getMedicineId() dolu
         *  - start ve end null değil
         */

        if (medication.getMedicine() == null || medication.getMedicine().getMedicineId() == null) {
            throw new IllegalArgumentException("Medication için medicine bilgisi zorunlu.");
        }
        if (medication.getStart() == null || medication.getEnd() == null) {
            throw new IllegalArgumentException("Medication için start ve end tarihleri zorunludur.");
        }
        if (medication.getEnd().isBefore(medication.getStart())) {
            throw new IllegalArgumentException("End tarihi start tarihinden önce olamaz.");
        }

        String medicineId = medication.getMedicine().getMedicineId();

        Medicine medicine = medicineRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Medicine bulunamadı: " + medicineId));

        // Managed entity set et
        medication.setMedicine(medicine);

        return medicationRepository.save(medication);
    }

    @Override
    public Medication updateMedication(String medicationId, Medication updatedMedication) {
        Medication existing = medicationRepository.findByMedicationId(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("Medication bulunamadı: " + medicationId));

        // Instructions güncelle
        if (updatedMedication.getInstructions() != null) {
            existing.setInstructions(updatedMedication.getInstructions());
        }

        // Tarih güncelle
        if (updatedMedication.getStart() != null) {
            existing.setStart(updatedMedication.getStart());
        }
        if (updatedMedication.getEnd() != null) {
            existing.setEnd(updatedMedication.getEnd());
        }

        // Tarih tutarlılığı (varsa)
        if (existing.getStart() != null && existing.getEnd() != null &&
                existing.getEnd().isBefore(existing.getStart())) {
            throw new IllegalArgumentException("End tarihi start tarihinden önce olamaz.");
        }

        // Medicine değişimi isteniyorsa
        if (updatedMedication.getMedicine() != null &&
                updatedMedication.getMedicine().getMedicineId() != null) {

            String newMedicineId = updatedMedication.getMedicine().getMedicineId();
            Medicine newMedicine = medicineRepository.findByMedicineId(newMedicineId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni medicine bulunamadı: " + newMedicineId));
            existing.setMedicine(newMedicine);
        }

        return medicationRepository.save(existing);
    }

    @Override
    public void deleteMedication(String medicationId) {
        boolean exists = medicationRepository.existsById(medicationId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen medication bulunamadı: " + medicationId);
        }

        medicationRepository.deleteById(medicationId);
    }
}
