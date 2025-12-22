package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;
import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.model.Medication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        String fullName = r.getVeterinary().getFirstName() + " " + r.getVeterinary().getLastName();
        dto.setVetName(r.getVeterinary() != null ? fullName : null);

        String clinicName = r.getVeterinary().getClinic().getName();
        dto.setClinicName(r.getVeterinary() != null ? clinicName : null);

        if (r.getMedications() != null) {
            List<MedicationResponseDTO> meds = new ArrayList<>();

            for (Medication m : r.getMedications()) {
                MedicationResponseDTO dtoMed = new MedicationResponseDTO();

                dtoMed.setMedicationId(m.getMedicationId());
                dtoMed.setInstructions(m.getInstructions());
                dtoMed.setStart(m.getStart());
                dtoMed.setEnd(m.getEnd());

                dtoMed.setRecordId(r.getRecordId());
                dtoMed.setPetId(r.getPet().getPetId());

                if (m.getMedicine() != null) {
                    dtoMed.setMedicineId(m.getMedicine().getMedicineId());
                    dtoMed.setMedicineName(m.getMedicine().getName());
                    dtoMed.setMedicineType(m.getMedicine().getType());
                }

                meds.add(dtoMed);
            }

            dto.setMedications(meds);
        }



        return dto;
    }
}
