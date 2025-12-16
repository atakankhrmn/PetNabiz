package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.petowner.PetOwnerResponseDTO;

import java.util.List;

public interface PetOwnerService {

    List<PetOwnerResponseDTO> getAllPetOwners();

    PetOwnerResponseDTO getPetOwnerById(String ownerId);

    PetOwnerResponseDTO getPetOwnerByEmail(String email);

    List<PetOwnerResponseDTO> searchPetOwnersByFirstName(String firstNamePart);

    List<PetOwnerResponseDTO> searchPetOwnersByLastName(String lastNamePart);

    PetOwnerResponseDTO createPetOwner(PetOwnerCreateRequestDTO dto);

    PetOwnerResponseDTO updatePetOwner(String ownerId, PetOwnerUpdateRequestDTO dto);

    void deletePetOwner(String ownerId);
}
