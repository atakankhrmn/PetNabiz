package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.PetOwner;

import java.util.List;
import java.util.Optional;

public interface PetOwnerService {

    // Tüm owner'lar
    List<PetOwner> getAllPetOwners();

    // ID ile owner bul
    Optional<PetOwner> getPetOwnerById(String ownerId);

    // User email ile owner bul (login sonrası vs.)
    Optional<PetOwner> getPetOwnerByEmail(String email);

    // İsim bazlı aramalar
    List<PetOwner> searchPetOwnersByFirstName(String firstNamePart);

    List<PetOwner> searchPetOwnersByLastName(String lastNamePart);

    // Yeni owner oluştur
    PetOwner createPetOwner(PetOwner petOwner);

    // Var olan owner'ı güncelle
    PetOwner updatePetOwner(String ownerId, PetOwner updatedOwner);

    // Owner sil
    void deletePetOwner(String ownerId);
}