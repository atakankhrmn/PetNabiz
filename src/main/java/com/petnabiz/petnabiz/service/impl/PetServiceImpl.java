package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.repository.PetOwnerRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.service.PetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;

    // Constructor injection
    public PetServiceImpl(PetRepository petRepository,
                          PetOwnerRepository petOwnerRepository) {
        this.petRepository = petRepository;
        this.petOwnerRepository = petOwnerRepository;
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public Optional<Pet> getPetById(String petId) {
        return petRepository.findByPetId(petId);
        // veya: return petRepository.findById(petId);
    }

    @Override
    public List<Pet> getPetsByOwnerId(String ownerId) {
        return petRepository.findByOwner_OwnerId(ownerId);
    }

    @Override
    public List<Pet> searchPetsByName(String namePart) {
        return petRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    public List<Pet> getPetsBySpecies(String species) {
        return petRepository.findBySpeciesIgnoreCase(species);
    }

    /*
    @Override
    public List<Pet> getPetsBySpeciesAndBreed(String species, String breed) {
        return petRepository.findBySpeciesIgnoreCaseAndBreedIgnoreCase(species, breed);
    }
    */


    //bu parametre olan peti nereden alıyor????
    //application classında mı olacak, controllerda mı olacak??
    @Override
    public Pet createPet(Pet pet) {
        // 1) Owner gerçekten var mı?
        if (pet.getOwner() == null || pet.getOwner().getOwnerId() == null) {
            throw new IllegalArgumentException("Pet için owner bilgisi zorunlu.");
        }

        String ownerId = pet.getOwner().getOwnerId();

        PetOwner owner = petOwnerRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner bulunamadı: " + ownerId));

        // 2) Pet'e owner'ı yeniden set et (DB'den gelen managed entity)
        pet.setOwner(owner);

        // 3) Kaydet
        return petRepository.save(pet);
    }

    @Override
    public Pet updatePet(String petId, Pet updatedPet) {
        // Önce eski pet'i çek
        Pet existingPet = petRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + petId));

        // Sadece modelde var olan field'leri güncelliyoruz
        existingPet.setName(updatedPet.getName());
        existingPet.setSpecies(updatedPet.getSpecies());
        existingPet.setBreed(updatedPet.getBreed());
        existingPet.setGender(updatedPet.getGender());
        existingPet.setPhotoUrl(updatedPet.getPhotoUrl());
        existingPet.setBirthDate(updatedPet.getBirthDate());
        existingPet.setWeight(updatedPet.getWeight());

        // Owner değişimi de desteklemek istersen:
        if (updatedPet.getOwner() != null && updatedPet.getOwner().getOwnerId() != null) {
            String newOwnerId = updatedPet.getOwner().getOwnerId();
            PetOwner newOwner = petOwnerRepository.findByOwnerId(newOwnerId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni owner bulunamadı: " + newOwnerId));
            existingPet.setOwner(newOwner);
        }

        return petRepository.save(existingPet);
    }

    @Override
    public void deletePet(String petId) {
        boolean exists = petRepository.existsByPetId(petId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen pet bulunamadı: " + petId);
        }
        //petOwner'dan da silinecek mi????
        //Cardinality'den dolayı iki tablodan da siliyor -Atakan
        petRepository.deleteById(petId);
    }
}
