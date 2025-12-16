package com.petnabiz.petnabiz.dto.request.medication;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicationUpdateRequestDTO {
    private String medicineId;
    private String recordId;
    private String instructions;
    private LocalDate start;
    private LocalDate end;
}
