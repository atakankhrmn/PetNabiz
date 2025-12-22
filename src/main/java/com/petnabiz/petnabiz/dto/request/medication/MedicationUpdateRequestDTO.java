package com.petnabiz.petnabiz.dto.request.medication;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicationUpdateRequestDTO {

    // ✅ EKLENEN KRİTİK ALAN: Hangi reçete satırını güncellediğimizi bilmek için şart
    private String medicationId;

    private String medicineId; // İlaç türü (Parol, Aspirin vb.) ID'si
    private String recordId;   // Bağlı olduğu kayıt ID'si
    private String instructions;
    private LocalDate start;
    private LocalDate end;
}