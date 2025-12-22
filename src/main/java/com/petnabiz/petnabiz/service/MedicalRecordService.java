package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface MedicalRecordService {

    List<MedicalRecordResponseDTO> getAllMedicalRecords();

    MedicalRecordResponseDTO getMedicalRecordById(String recordId);

    List<MedicalRecordResponseDTO> getMedicalRecordsByPetId(String petId);

    List<MedicalRecordResponseDTO> getMedicalRecordsByVeterinaryId(String vetId);

    List<MedicalRecordResponseDTO> getMedicalRecordsByDate(LocalDate date);

    List<MedicalRecordResponseDTO> getMedicalRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    MedicalRecordResponseDTO createMedicalRecord(MedicalRecordCreateRequestDTO dto);

    MedicalRecordResponseDTO updateMedicalRecord(String recordId, MedicalRecordUpdateRequestDTO dto);

    void deleteMedicalRecord(String recordId);

    boolean isPetOwnedBy(String ownerEmail, String petId);
    boolean isRecordOwnedBy(String ownerEmail, String recordId);

}
