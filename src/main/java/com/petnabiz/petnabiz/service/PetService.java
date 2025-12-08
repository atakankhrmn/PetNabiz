package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Pet;

import java.util.List;
import java.util.Optional;

public interface PetService {

    // Tüm pet'leri listele
    List<Pet> getAllPets();

    // ID ile pet bul
    Optional<Pet> getPetById(String petId);

    // Owner'a göre pet listesi
    List<Pet> getPetsByOwnerId(String ownerId);

    // İsim arama
    List<Pet> searchPetsByName(String namePart);

    // Tür/ırk filtre
    List<Pet> getPetsBySpecies(String species);

    /*
    List<Pet> getPetsBySpeciesAndBreed(String species, String breed);
    */

    // Yeni pet oluştur
    Pet createPet(Pet pet);

    // Var olan pet'i güncelle
    Pet updatePet(String petId, Pet updatedPet);

    // Pet sil
    void deletePet(String petId);

    /*
    List<Pet> getPetsByClinicId(String clinicId);
     */
}
