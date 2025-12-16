package com.petnabiz.petnabiz.dto.request.medicalrecord;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicalRecordUpdateRequestDTO {
    private String description;
    private LocalDate date;
    private String petId;
    private String vetId;
}
