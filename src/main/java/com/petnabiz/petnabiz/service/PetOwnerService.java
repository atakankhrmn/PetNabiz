package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.petowner.PetOwnerResponseDTO;

import java.util.List;

public interface PetOwnerService {

    // ADMIN
    List<PetOwnerResponseDTO> getAllPetOwners();

    // ADMIN or OWNER self
    PetOwnerResponseDTO getPetOwnerById(String ownerId);

    // ADMIN only (email üzerinden başkasını çekmek privacy)
    PetOwnerResponseDTO getPetOwnerByEmail(String email);

    // ADMIN only
    List<PetOwnerResponseDTO> searchPetOwnersByFirstName(String firstNamePart);
    List<PetOwnerResponseDTO> searchPetOwnersByLastName(String lastNamePart);

    // register (permitAll ya da admin)
    PetOwnerResponseDTO createPetOwner(PetOwnerCreateRequestDTO dto);

    // ADMIN or OWNER self
    PetOwnerResponseDTO updatePetOwner(String ownerId, PetOwnerUpdateRequestDTO dto);

    // ADMIN only (istersen self-delete açarız)
    void deletePetOwner(String ownerId);

    // OWNER için “ben”
    PetOwnerResponseDTO getMyProfile();

    // Security helper (SpEL)
    boolean isSelf(String ownerId, String email);
}
