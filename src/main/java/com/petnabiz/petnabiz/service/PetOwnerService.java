package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.PetOwner;

import java.util.List;
import java.util.Optional;

public interface PetOwnerService {

    // Tüm owner'lar
    List<PetOwner> getAllOwners();

    // ID ile owner bul
    Optional<PetOwner> getOwnerById(String ownerId);

    // User email ile owner bul (login sonrası vs.)
    Optional<PetOwner> getOwnerByEmail(String email);

    // İsim bazlı aramalar
    List<PetOwner> searchOwnersByFirstName(String firstNamePart);

    List<PetOwner> searchOwnersByLastName(String lastNamePart);

    // Yeni owner oluştur
    PetOwner createOwner(PetOwner petOwner);

    // Var olan owner'ı güncelle
    PetOwner updateOwner(String ownerId, PetOwner updatedOwner);

    // Owner sil
    void deleteOwner(String ownerId);
}
