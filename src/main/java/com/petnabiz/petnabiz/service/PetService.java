package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.pet.PetCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.pet.PetUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;

import java.util.List;

public interface PetService {

    List<PetResponseDTO> getAllPets();

    PetResponseDTO getPetById(String petId);

    List<PetResponseDTO> getPetsByOwnerId(String ownerId);

    List<PetResponseDTO> searchPetsByName(String namePart);

    List<PetResponseDTO> getPetsBySpecies(String species);

    PetResponseDTO createPet(PetCreateRequestDTO dto);

    PetResponseDTO updatePet(String petId, PetUpdateRequestDTO dto);

    void deletePet(String petId);
}
