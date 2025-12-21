package com.petnabiz.petnabiz.dto.request.medicalrecord;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicalRecordCreateRequestDTO {
    private String description;   // opsiyonel
    private LocalDate date;       // zorunlu
    private String petId;         // zorunlu
    private String vetId;         // zorunlu
}
