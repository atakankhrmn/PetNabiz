package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import com.petnabiz.petnabiz.model.Medication;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapper {

    public MedicationResponseDTO toResponse(Medication m) {
        if (m == null) {
            return null;
        }

        MedicationResponseDTO dto = new MedicationResponseDTO();

        // Temel Bilgiler
        dto.setMedicationId(m.getMedicationId());
        dto.setInstructions(m.getInstructions());
        dto.setStart(m.getStart());
        dto.setEnd(m.getEnd());

        // Medical Record ve Pet Bağlantısı
        if (m.getMedicalRecord() != null) {
            dto.setRecordId(m.getMedicalRecord().getRecordId());

            if (m.getMedicalRecord().getPet() != null) {
                dto.setPetId(m.getMedicalRecord().getPet().getPetId());
            }
        }

        // İlaç (Medicine) Detayları
        // try-catch bloklarına gerek yok, null kontrolü yeterli.
        if (m.getMedicine() != null) {
            dto.setMedicineId(m.getMedicine().getMedicineId());
            dto.setMedicineName(m.getMedicine().getName());
            dto.setMedicineType(m.getMedicine().getType());
        }

        return dto;
    }
}