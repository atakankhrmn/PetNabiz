package com.petnabiz.petnabiz.dto.request.medication;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicationCreateRequestDTO {
    private String medicationId;   // opsiyonel (string id)
    private String medicineId;     // zorunlu
    private String recordId;       // opsiyonel ama genelde bağlanacak
    private String instructions;   // opsiyonel
    private LocalDate start;       // zorunlu
    private LocalDate end;         // zorunlu
}
