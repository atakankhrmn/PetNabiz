package com.petnabiz.petnabiz.dto.response.vaccine;

import lombok.Data;
import java.time.LocalDate;
import com.petnabiz.petnabiz.dto.summary.PetSummaryDTO;

@Data
public class VaccineResponseDTO {
    private String vaccineId;
    private String vaccineName;
    private LocalDate dateApplied;
    private LocalDate nextDueDate;
    private PetSummaryDTO pet;
}
