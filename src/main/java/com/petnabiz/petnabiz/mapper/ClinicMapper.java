package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.clinic.ClinicResponseDTO;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClinicMapper {

    public ClinicResponseDTO toResponse(Clinic clinic) {
        ClinicResponseDTO dto = new ClinicResponseDTO();
        dto.setClinicId(clinic.getClinicId());

        dto.setName(clinic.getName());
        dto.setCity(clinic.getCity());
        dto.setDistrict(clinic.getDistrict());
        dto.setAddress(clinic.getAddress());
        dto.setPhone(clinic.getPhone());

        if (clinic.getUser() != null) {
            dto.setEmail(clinic.getUser().getEmail());
            dto.setActive(clinic.getUser().isActive());
        }

        // veterinaries (lazy olabilir; güvenli olsun diye list null ise boş bırakıyoruz)
        if (clinic.getVeterinaries() != null) {
            List<VetSummaryDTO> vets = clinic.getVeterinaries().stream().map(this::toVetSummary).toList();
            dto.setVeterinaries(vets);
        }

        return dto;
    }

    public VetSummaryDTO toVetSummary(Veterinary v) {
        VetSummaryDTO dto = new VetSummaryDTO();
        dto.setVetId(v.getVetId());
        dto.setFirstName(v.getFirstName());
        dto.setLastName(v.getLastName());
        return dto;
    }
}
