package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwner, String> {

    // 1) ID bazlı erişim (domain-friendly)
    Optional<PetOwner> findByOwnerId(String ownerId);
    boolean existsByOwnerId(String ownerId);


    // 2) USER ilişkisi üzerinden erişim (login / auth için kritik)
    Optional<PetOwner> findByUser(User user);

    Optional<PetOwner> findByUser_UserId(String userId);

    Optional<PetOwner> findByUser_Email(String email);

    boolean existsByUser_Email(String email);


    // 3) İsim bazlı aramalar
    List<PetOwner> findByFirstNameIgnoreCase(String firstName);

    List<PetOwner> findByLastNameIgnoreCase(String lastName);

    List<PetOwner> findByFirstNameContainingIgnoreCase(String namePart);

    List<PetOwner> findByLastNameContainingIgnoreCase(String namePart);

    // Ad + soyad birlikte arama
    List<PetOwner> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);


    // 4) Telefon + adres sorguları
    Optional<PetOwner> findByPhone(String phone);

    List<PetOwner> findByAddressContainingIgnoreCase(String addressPart);
}
