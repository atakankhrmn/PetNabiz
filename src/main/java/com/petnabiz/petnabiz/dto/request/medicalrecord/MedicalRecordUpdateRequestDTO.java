package com.petnabiz.petnabiz.dto.request.medicalrecord;

import com.petnabiz.petnabiz.dto.request.medication.MedicationUpdateRequestDTO; // Bunu import etmeyi unutma
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MedicalRecordUpdateRequestDTO {
    private String description;
    private LocalDate date;
    private String petId;
    private String vetId;

    // ✅ YENİ EKLENEN KISIM:
    private List<MedicationUpdateRequestDTO> medications;
}