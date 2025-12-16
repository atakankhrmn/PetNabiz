package com.petnabiz.petnabiz.dto.response.medication;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicationResponseDTO {
    private String medicationId;
    private String instructions;
    private LocalDate start;
    private LocalDate end;

    private String recordId;
    private String petId;

    private String medicineId;
    private String medicineName;
    private String medicineType;
}
