package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;
import org.springframework.stereotype.Component;

@Component
public class VeterinaryMapper {

    public VeterinaryResponseDTO toResponse(Veterinary v) {
        VeterinaryResponseDTO dto = new VeterinaryResponseDTO();
        dto.setVetId(v.getVetId());
        dto.setFirstName(v.getFirstName());
        dto.setLastName(v.getLastName());
        dto.setPhoneNumber(v.getPhoneNumber());
        dto.setAddress(v.getAddress());
        dto.setCertificate(v.getCertificate());

        Clinic c = v.getClinic();
        if (c != null) {
            dto.setClinicId(c.getClinicId());
            dto.setClinicName(c.getName());
        }
        return dto;
    }
}
