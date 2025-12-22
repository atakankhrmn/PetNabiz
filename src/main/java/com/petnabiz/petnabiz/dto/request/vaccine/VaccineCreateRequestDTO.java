package com.petnabiz.petnabiz.dto.request.vaccine;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VaccineCreateRequestDTO {
    private String petId;
    private String vaccineName;
    private LocalDate dateApplied;
    private LocalDate nextDueDate;
}
