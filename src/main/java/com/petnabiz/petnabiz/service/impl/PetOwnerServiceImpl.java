package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.PetOwnerRepository;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.PetOwnerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetOwnerServiceImpl implements PetOwnerService {

    private final PetOwnerRepository petOwnerRepository;
    private final UserRepository userRepository;

    public PetOwnerServiceImpl(PetOwnerRepository petOwnerRepository,
                               UserRepository userRepository) {
        this.petOwnerRepository = petOwnerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PetOwner> getAllPetOwners() {
        return petOwnerRepository.findAll();
    }

    @Override
    public Optional<PetOwner> getPetOwnerById(String ownerId) {
        return petOwnerRepository.findByOwnerId(ownerId);
        // veya: return petOwnerRepository.findById(ownerId);
    }

    @Override
    public Optional<PetOwner> getPetOwnerByEmail(String email) {
        return petOwnerRepository.findByUser_Email(email);
    }

    @Override
    public List<PetOwner> searchPetOwnersByFirstName(String firstNamePart) {
        return petOwnerRepository.findByFirstNameContainingIgnoreCase(firstNamePart);
    }

    @Override
    public List<PetOwner> searchPetOwnersByLastName(String lastNamePart) {
        return petOwnerRepository.findByLastNameContainingIgnoreCase(lastNamePart);
    }

    @Override
    public PetOwner createPetOwner(PetOwner petOwner) {
        /*
         * Tasarımın gereği:
         * PetOwner.ownerId = User.userId (MapsId)
         * O yüzden:
         *  - Elimizdeki PetOwner için User gerçekten var mı kontrol ediyoruz
         *  - DB'den gerçek User nesnesini çekip PetOwner'a set ediyoruz
         */

        if (petOwner.getUser() == null) {
            throw new IllegalArgumentException("PetOwner için user bilgisi zorunlu.");
        }

        User ownerUser;

        // Öncelik: userId doluysa userId üzerinden git
        if (petOwner.getUser().getUserId() != null) {
            String userId = petOwner.getUser().getUserId();
            ownerUser = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + userId));
        } else if (petOwner.getUser().getEmail() != null) {
            // userId yok ama email varsa email'den bulmayı dene
            String email = petOwner.getUser().getEmail();
            ownerUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + email));
        } else {
            throw new IllegalArgumentException("User için userId veya email bilgisi sağlanmalı.");
        }

        // MapsId: ownerId = userId
        petOwner.setOwnerId(ownerUser.getUserId());
        petOwner.setUser(ownerUser);

        return petOwnerRepository.save(petOwner);
    }

    @Override
    public PetOwner updatePetOwner(String ownerId, PetOwner updatedOwner) {
        PetOwner existingOwner = petOwnerRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("PetOwner bulunamadı: " + ownerId));

        // Sadece PetOwner alanlarını güncelliyoruz (User login bilgilerine dokunmuyoruz)
        existingOwner.setFirstName(updatedOwner.getFirstName());
        existingOwner.setLastName(updatedOwner.getLastName());
        existingOwner.setPhone(updatedOwner.getPhone());
        existingOwner.setAddress(updatedOwner.getAddress());

        return petOwnerRepository.save(existingOwner);
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
