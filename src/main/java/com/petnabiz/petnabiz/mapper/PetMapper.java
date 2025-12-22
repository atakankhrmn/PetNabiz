package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;
import com.petnabiz.petnabiz.model.Pet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetMapper {

    public PetResponseDTO toResponse(Pet p) {
        PetResponseDTO dto = new PetResponseDTO();
        dto.setPetId(p.getPetId());
        dto.setName(p.getName());
        dto.setSpecies(p.getSpecies());
        dto.setBreed(p.getBreed());
        dto.setGender(p.getGender());
        dto.setPhotoUrl(p.getPhotoUrl());
        dto.setBirthDate(p.getBirthDate());
        dto.setWeight(p.getWeight());

        dto.setOwnerId(p.getOwner() != null ? p.getOwner().getOwnerId() : null);

        String ownerName = p.getOwner().getFirstName() + " " + p.getOwner().getLastName();
        dto.setOwnerName(ownerName);

        if (p.getAppointments() != null) {
            List<String> ids = p.getAppointments().stream()
                    .map(a -> a.getAppointmentId())
                    .toList();
            dto.setAppointmentIds(ids);
        }

        if (p.getMedicalRecords() != null) {
            List<String> ids = p.getMedicalRecords().stream()
                    .map(r -> r.getRecordId())
                    .toList();
            dto.setMedicalRecordIds(ids);
        }

        return dto;
    }
}
