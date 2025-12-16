package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.petowner.PetOwnerResponseDTO;
import com.petnabiz.petnabiz.model.PetOwner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetOwnerMapper {

    public PetOwnerResponseDTO toResponse(PetOwner o) {
        PetOwnerResponseDTO dto = new PetOwnerResponseDTO();
        dto.setOwnerId(o.getOwnerId());
        dto.setFirstName(o.getFirstName());
        dto.setLastName(o.getLastName());
        dto.setPhone(o.getPhone());
        dto.setAddress(o.getAddress());

        if (o.getUser() != null) {
            dto.setEmail(o.getUser().getEmail());
            dto.setActive(o.getUser().isActive());
        }

        if (o.getPets() != null) {
            List<String> ids = o.getPets().stream()
                    .map(p -> p.getPetId())
                    .toList();
            dto.setPetIds(ids);
        }

        return dto;
    }
}

