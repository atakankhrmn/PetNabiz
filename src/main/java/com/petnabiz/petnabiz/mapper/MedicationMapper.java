package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import com.petnabiz.petnabiz.model.Medication;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapper {

    public MedicationResponseDTO toResponse(Medication m) {
        MedicationResponseDTO dto = new MedicationResponseDTO();
        dto.setMedicationId(m.getMedicationId());
        dto.setInstructions(m.getInstructions());
        dto.setStart(m.getStart());
        dto.setEnd(m.getEnd());

        if (m.getMedicalRecord() != null) {
            dto.setRecordId(m.getMedicalRecord().getRecordId());
            if (m.getMedicalRecord().getPet() != null) {
                dto.setPetId(m.getMedicalRecord().getPet().getPetId());
            }
        }

        if (m.getMedicine() != null) {
            dto.setMedicineId(m.getMedicine().getMedicineId());
            // Medicine modelinde varsa:
            try { dto.setMedicineName(m.getMedicine().getName()); } catch (Exception ignored) {}
            try { dto.setMedicineType(m.getMedicine().getType()); } catch (Exception ignored) {}
        }

        return dto;
    }
}
