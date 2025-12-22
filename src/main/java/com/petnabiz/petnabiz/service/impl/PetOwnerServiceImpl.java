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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("petOwnerService") // SpEL için
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

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("Authentication yok.");
        return auth.getName(); // username=email
    }

    @Override
    public boolean isSelf(String ownerId, String email) {
        if (ownerId == null || ownerId.isBlank() || email == null || email.isBlank()) return false;

        PetOwner owner = petOwnerRepository.findByOwnerId(ownerId).orElse(null);
        if (owner == null || owner.getUser() == null || owner.getUser().getEmail() == null) return false;

        return email.equalsIgnoreCase(owner.getUser().getEmail());
    }

    @Override
    public List<PetOwnerResponseDTO> getAllPetOwners() {
        return petOwnerRepository.findAll().stream()
                .map(petOwnerMapper::toResponse)
                .toList();
    }

    @Override
    public PetOwnerResponseDTO getMyProfile() {
        String email = currentEmail();
        PetOwner owner = petOwnerRepository.findByUser_Email(email)
                .orElseThrow(() -> new EntityNotFoundException("PetOwner bulunamadı (me): " + email));
        return petOwnerMapper.toResponse(owner);
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

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
        }
        if (petOwnerRepository.existsByOwnerId(dto.getOwnerId())) {
            throw new IllegalArgumentException("Bu ownerId zaten kullanılıyor: " + dto.getOwnerId());
        }

        User user = new User();
        user.setUserId(dto.getOwnerId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO hash
        user.setRole("ROLE_OWNER");
        user.setActive(true);
        userRepository.save(user);

        PetOwner owner = new PetOwner();
        owner.setOwnerId(user.getUserId());
        owner.setUser(user);

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("firstName zorunlu.");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("lastName zorunlu.");
        }
        if (dto.getPhone() == null || dto.getPhone().isBlank()) {
            throw new IllegalArgumentException("phone zorunlu.");
        }
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw new IllegalArgumentException("address zorunlu.");
        }

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

        if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) existing.setLastName(dto.getLastName());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());

        if (existing.getUser() == null) {
            throw new IllegalStateException("PetOwner user kaydı yok: " + ownerId);
        }

        // Email update: sadece admin yapsın dersen burada role check koyarız.
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
    @Transactional
    public void deletePetOwner(String ownerId) {
        PetOwner existing = petOwnerRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen PetOwner bulunamadı: " + ownerId));

        // FK vs varsa önce child cleanup gerekir, yoksa hata alırsın (pets vs)
        petOwnerRepository.delete(existing);
    }
}
