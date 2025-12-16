package com.petnabiz.petnabiz.dto.response.medicalrecord;

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

    private List<String> medicationIds; // şimdilik ID listesi (en garanti)
}
