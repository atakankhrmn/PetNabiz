package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.petowner.PetOwnerResponseDTO;
import com.petnabiz.petnabiz.mapper.PetOwnerMapper;
import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.PetOwnerRepository;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.PetOwnerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetOwnerServiceImpl implements PetOwnerService {

    private final PetOwnerRepository petOwnerRepository;
    private final UserRepository userRepository;
    private final PetOwnerMapper petOwnerMapper;

    public PetOwnerServiceImpl(PetOwnerRepository petOwnerRepository,
                               UserRepository userRepository,
                               PetOwnerMapper petOwnerMapper) {
        this.petOwnerRepository = petOwnerRepository;
        this.userRepository = userRepository;
        this.petOwnerMapper = petOwnerMapper;
    }

    @Override
    public List<PetOwnerResponseDTO> getAllPetOwners() {
        return petOwnerRepository.findAll().stream()
                .map(petOwnerMapper::toResponse)
                .toList();
    }

    @Override
    public PetOwnerResponseDTO getPetOwnerById(String ownerId) {
        PetOwner o = petOwnerRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("PetOwner bulunamadı: " + ownerId));
        return petOwnerMapper.toResponse(o);
    }

    @Override
    public PetOwnerResponseDTO getPetOwnerByEmail(String email) {
        PetOwner o = petOwnerRepository.findByUser_Email(email)
                .orElseThrow(() -> new EntityNotFoundException("PetOwner bulunamadı (email): " + email));
        return petOwnerMapper.toResponse(o);
    }

    @Override
    public List<PetOwnerResponseDTO> searchPetOwnersByFirstName(String firstNamePart) {
        return petOwnerRepository.findByFirstNameContainingIgnoreCase(firstNamePart)
                .stream()
                .map(petOwnerMapper::toResponse)
                .toList();
    }

    @Override
    public List<PetOwnerResponseDTO> searchPetOwnersByLastName(String lastNamePart) {
        return petOwnerRepository.findByLastNameContainingIgnoreCase(lastNamePart)
                .stream()
                .map(petOwnerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PetOwnerResponseDTO createPetOwner(PetOwnerCreateRequestDTO dto) {

        if (dto.getOwnerId() == null || dto.getOwnerId().isBlank()) {
            throw new IllegalArgumentException("ownerId (userId) zorunlu.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("email zorunlu.");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("password zorunlu.");
        }

        // email benzersiz mi?
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
        }

        // user oluştur
        User user = new User();
        user.setUserId(dto.getOwnerId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO hash
        user.setRole("ROLE_OWNER");
        user.setActive(true);
        userRepository.save(user);

        // petOwner oluştur
        PetOwner owner = new PetOwner();
        owner.setOwnerId(user.getUserId());
        owner.setUser(user);
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());

        PetOwner saved = petOwnerRepository.save(owner);
        return petOwnerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PetOwnerResponseDTO updatePetOwner(String ownerId, PetOwnerUpdateRequestDTO dto) {

        PetOwner existing = petOwnerRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("PetOwner bulunamadı: " + ownerId));

        // owner alanları
        if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) existing.setLastName(dto.getLastName());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());

        // user alanları
        if (existing.getUser() == null) {
            throw new IllegalStateException("PetOwner user kaydı yok: " + ownerId);
        }

        if (dto.getEmail() != null) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getUserId().equals(existing.getUser().getUserId())) {
                    throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
                }
            });
            existing.getUser().setEmail(dto.getEmail());
        }

        if (dto.getActive() != null) {
            existing.getUser().setActive(dto.getActive());
        }

        PetOwner saved = petOwnerRepository.save(existing);
        return petOwnerMapper.toResponse(saved);
    }

    @Override
    public void deletePetOwner(String ownerId) {
        boolean exists = petOwnerRepository.existsByOwnerId(ownerId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen PetOwner bulunamadı: " + ownerId);
        }
        petOwnerRepository.deleteById(ownerId);
    }
}
