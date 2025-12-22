package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.medication.MedicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medication.MedicationUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface MedicationService {

    List<MedicationResponseDTO> getAllMedications();

    MedicationResponseDTO getMedicationById(String medicationId);

    List<MedicationResponseDTO> getMedicationsByPetId(String petId);

    List<MedicationResponseDTO> getMedicationsByMedicalRecordId(String recordId);

    List<MedicationResponseDTO> getMedicationsByMedicineId(String medicineId);

    List<MedicationResponseDTO> searchByMedicineName(String namePart);

    List<MedicationResponseDTO> getMedicationsByMedicineType(String type);

    List<MedicationResponseDTO> getActiveMedicationsOn(LocalDate date);

    List<MedicationResponseDTO> getMedicationsBetween(LocalDate start, LocalDate end);

    MedicationResponseDTO createMedication(MedicationCreateRequestDTO dto);

    MedicationResponseDTO updateMedication(String medicationId, MedicationUpdateRequestDTO dto);

    void deleteMedication(String medicationId);
    boolean isPetOwnedBy(String ownerEmail, String petId);
    boolean isRecordOwnedBy(String ownerEmail, String recordId);
    boolean isMedicationOwnedBy(String ownerEmail, String medicationId);


}
