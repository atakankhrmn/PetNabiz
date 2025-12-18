package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.pet.PetCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.pet.PetUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;

import java.util.List;

public interface PetService {

    // ADMIN
    List<PetResponseDTO> getAllPets();

    // ADMIN + (OWNER own-check via @PreAuthorize)
    PetResponseDTO getPetById(String petId);

    // OWNER (current user) - /api/pets/my
    List<PetResponseDTO> getMyPets();

    // ADMIN (ownerId ile listeleme)
    List<PetResponseDTO> getPetsByOwnerId(String ownerId);

    // ADMIN + OWNER (owner self-enforced in service for create/update)
    PetResponseDTO createPet(PetCreateRequestDTO dto);

    PetResponseDTO updatePet(String petId, PetUpdateRequestDTO dto);

    String uploadPetPhoto(String petId, org.springframework.web.multipart.MultipartFile file);

    void deletePet(String petId);

    // Security helper for SpEL
    boolean isPetOwnedBy(String ownerEmail, String petId);
}
