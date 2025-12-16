package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.pet.PetCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.pet.PetUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;
import com.petnabiz.petnabiz.mapper.PetMapper;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.repository.PetOwnerRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.service.PetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetMapper petMapper;

    public PetServiceImpl(PetRepository petRepository,
                          PetOwnerRepository petOwnerRepository,
                          PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.petMapper = petMapper;
    }

    @Override
    public List<PetResponseDTO> getAllPets() {
        return petRepository.findAll().stream()
                .map(petMapper::toResponse)
                .toList();
    }

    @Override
    public PetResponseDTO getPetById(String petId) {
        Pet p = petRepository.findByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet bulunamadı: " + petId));
        return petMapper.toResponse(p);
    }

    @Override
    public List<PetResponseDTO> getPetsByOwnerId(String ownerId) {
        return petRepository.findByOwner_OwnerId(ownerId).stream()
                .map(petMapper::toResponse)
                .toList();
    }

    @Override
    public List<PetResponseDTO> searchPetsByName(String namePart) {
        return petRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(petMapper::toResponse)
                .toList();
    }

    @Override
    public List<PetResponseDTO> getPetsBySpecies(String species) {
        return petRepository.findBySpeciesIgnoreCase(species).stream()
                .map(petMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PetResponseDTO createPet(PetCreateRequestDTO dto) {

        if (dto.getOwnerId() == null || dto.getOwnerId().isBlank()) {
            throw new IllegalArgumentException("Pet için ownerId zorunlu.");
        }
        if (dto.getSpecies() == null || dto.getSpecies().isBlank()) {
            throw new IllegalArgumentException("Pet için species zorunlu.");
        }
        if (dto.getGender() == null || dto.getGender().isBlank()) {
            throw new IllegalArgumentException("Pet için gender zorunlu.");
        }

        PetOwner owner = petOwnerRepository.findByOwnerId(dto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner bulunamadı: " + dto.getOwnerId()));

        Pet pet = new Pet();

        if (dto.getPetId() != null && !dto.getPetId().isBlank()) {
            pet.setPetId(dto.getPetId());
        }

        pet.setName(dto.getName());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed());
        pet.setGender(dto.getGender());
        pet.setPhotoUrl(dto.getPhotoUrl());
        pet.setBirthDate(dto.getBirthDate());
        if (dto.getWeight() != null) pet.setWeight(dto.getWeight());

        pet.setOwner(owner);

        Pet saved = petRepository.save(pet);
        return petMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PetResponseDTO updatePet(String petId, PetUpdateRequestDTO dto) {

        Pet existing = petRepository.findByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet bulunamadı: " + petId));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getSpecies() != null) existing.setSpecies(dto.getSpecies());
        if (dto.getBreed() != null) existing.setBreed(dto.getBreed());
        if (dto.getGender() != null) existing.setGender(dto.getGender());
        if (dto.getPhotoUrl() != null) existing.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getBirthDate() != null) existing.setBirthDate(dto.getBirthDate());
        if (dto.getWeight() != null) existing.setWeight(dto.getWeight());

        if (dto.getOwnerId() != null && !dto.getOwnerId().isBlank()) {
            PetOwner newOwner = petOwnerRepository.findByOwnerId(dto.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Yeni owner bulunamadı: " + dto.getOwnerId()));
            existing.setOwner(newOwner);
        }

        Pet saved = petRepository.save(existing);
        return petMapper.toResponse(saved);
    }

    @Override
    public void deletePet(String petId) {
        boolean exists = petRepository.existsByPetId(petId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen pet bulunamadı: " + petId);
        }
        petRepository.deleteById(petId);
    }
}
