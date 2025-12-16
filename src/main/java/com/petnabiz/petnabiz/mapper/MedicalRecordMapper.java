package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;
import com.petnabiz.petnabiz.model.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicalRecordMapper {

    public MedicalRecordResponseDTO toResponse(MedicalRecord r) {
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        dto.setRecordId(r.getRecordId());
        dto.setDescription(r.getDescription());
        dto.setDate(r.getDate());

        dto.setPetId(r.getPet() != null ? r.getPet().getPetId() : null);
        dto.setVetId(r.getVeterinary() != null ? r.getVeterinary().getVetId() : null);

        if (r.getMedications() != null) {
            List<String> ids = r.getMedications().stream()
                    .map(m -> m.getMedicationId())
                    .toList();
            dto.setMedicationIds(ids);
        }

        return dto;
    }
}
