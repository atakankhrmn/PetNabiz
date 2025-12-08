package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Medication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicationService {

    // Tüm ilaç kayıtları
    List<Medication> getAllMedications();

    // ID ile tek medication
    Optional<Medication> getMedicationById(String medicationId);

    // Belirli bir medicineId'ye ait tüm medication kayıtları
    List<Medication> getMedicationsByMedicineId(String medicineId);

    // İlaç adına göre arama
    List<Medication> searchByMedicineName(String namePart);

    // Type'a göre filtre (tablet, syrup vs.)
    List<Medication> getMedicationsByMedicineType(String type);

    // Belirli bir tarihte aktif olan ilaçlar (start ≤ date ≤ end)
    List<Medication> getActiveMedicationsOn(LocalDate date);

    // Başlangıç & bitiş aralığı belirli bir range içinde olan ilaçlar
    List<Medication> getMedicationsBetween(LocalDate start, LocalDate end);

    // Yeni medication kaydı oluştur
    Medication createMedication(Medication medication);

    // Var olan medication'ı güncelle
    Medication updateMedication(String medicationId, Medication updatedMedication);

    // Medication sil
    void deleteMedication(String medicationId);
}
