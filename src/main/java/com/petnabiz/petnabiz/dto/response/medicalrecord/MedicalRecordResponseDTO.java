package com.petnabiz.petnabiz.dto.response.medicalrecord;

import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MedicalRecordResponseDTO {
    private String recordId;
    private String description;
    private LocalDate date;

    private String petId;
    private String vetId;
    private String vetName;
    private String clinicName;

    private List<MedicationResponseDTO> medications; // şimdilik ID listesi (en garanti)
}
