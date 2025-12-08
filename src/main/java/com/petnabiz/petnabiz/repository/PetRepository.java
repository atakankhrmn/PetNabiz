package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {

    // 1) ID bazlı erişim
    Optional<Pet> findByPetId(String petId);
    boolean existsByPetId(String petId);


    // 2) Owner bazlı aramalar
    List<Pet> findByOwner(PetOwner owner);

    List<Pet> findByOwner_OwnerId(String ownerId);


    // 3) Name search
    Optional<Pet> findByName(String name);

    List<Pet> findByNameContainingIgnoreCase(String namePart);


    // 4) Species & Breed bazlı filtreler
    List<Pet> findBySpeciesIgnoreCase(String species);

    List<Pet> findByBreedIgnoreCase(String breed);

    List<Pet> findBySpeciesIgnoreCaseAndBreedIgnoreCase(String species, String breed);


    // 5) Gender filtre
    List<Pet> findByGenderIgnoreCase(String gender);


    // 6) BirthDate & Age-related
    List<Pet> findByBirthDate(LocalDate birthDate);

    List<Pet> findByBirthDateBefore(LocalDate date);

    List<Pet> findByBirthDateAfter(LocalDate date);


    // 7) Weight filtreleri
    List<Pet> findByWeightGreaterThan(double weight);

    List<Pet> findByWeightLessThan(double weight);

    List<Pet> findByWeightBetween(double min, double max);

}
