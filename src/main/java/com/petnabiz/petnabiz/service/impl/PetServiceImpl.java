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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service("petService") // @PreAuthorize içinde @petService çağıracağız
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

    // ---------------------------
    // Security helper
    // ---------------------------
    @Override
    public boolean isPetOwnedBy(String ownerEmail, String petId) {
        if (ownerEmail == null || petId == null || petId.isBlank()) return false;

        Pet p = petRepository.findByPetId(petId).orElse(null);
        if (p == null) return false;

        if (p.getOwner() == null ||
                p.getOwner().getUser() == null ||
                p.getOwner().getUser().getEmail() == null) {
            return false;
        }

        return ownerEmail.equalsIgnoreCase(p.getOwner().getUser().getEmail());
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("Authentication yok.");
        return auth.getName(); // username = email (SecurityUserDetailsService)
    }

    // ---------------------------
    // Queries
    // ---------------------------

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

    /**
     * OWNER için "my" endpoint buradan çalışır.
     */
    @Override
    public List<PetResponseDTO> getMyPets() {
        String email = currentEmail();
        return petRepository.findByOwner_User_Email(email).stream()
                .map(petMapper::toResponse)
                .toList();
    }

    /**
     * Admin tarafı isterse kullanır.
     */
    @Override
    public List<PetResponseDTO> getPetsByOwnerId(String ownerId) {
        return petRepository.findByOwner_OwnerId(ownerId).stream()
                .map(petMapper::toResponse)
                .toList();
    }

    // ---------------------------
    // Mutations
    // ---------------------------

    @Override
    @Transactional

    public PetResponseDTO createPet(PetCreateRequestDTO dto) {

        if (dto.getSpecies() == null || dto.getSpecies().isBlank()) {
            throw new IllegalArgumentException("Pet için species zorunlu.");
        }
        if (dto.getGender() == null || dto.getGender().isBlank()) {
            throw new IllegalArgumentException("Pet için gender zorunlu.");
        }

        // OWNER ise ownerId'yi DTO'dan ALMIYORUZ. Kendinden çıkarıyoruz.
        // ADMIN ise dto.ownerId zorunlu.
        PetOwner owner;
        String email = currentEmail();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            if (dto.getOwnerId() == null || dto.getOwnerId().isBlank()) {
                throw new IllegalArgumentException("Admin pet oluştururken ownerId zorunlu.");
            }
            owner = petOwnerRepository.findByOwnerId(dto.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner bulunamadı: " + dto.getOwnerId()));
        } else {
            owner = petOwnerRepository.findByUser_Email(email)
                    .orElseThrow(() -> new IllegalArgumentException("Owner bulunamadı (email): " + email));
        }

        // ✅ petId backend'de üretilir (DTO'dan gelmez)
        String newPetId = UUID.randomUUID().toString();
        while (petRepository.existsByPetId(newPetId)) { // aşırı düşük ihtimal ama garanti
            newPetId = UUID.randomUUID().toString();
        }

        Pet pet = new Pet();
        pet.setPetId(newPetId);

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

        // OWNER ownerId değiştiramasın. Admin isterse değiştirsin.
        if (dto.getOwnerId() != null && !dto.getOwnerId().isBlank()) {
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new IllegalArgumentException("Owner, pet ownerId değiştiremez.");
            }

            PetOwner newOwner = petOwnerRepository.findByOwnerId(dto.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Yeni owner bulunamadı: " + dto.getOwnerId()));
            existing.setOwner(newOwner);
        }

        Pet saved = petRepository.save(existing);
        return petMapper.toResponse(saved);
    }

    // PetServiceImpl.java içine eklenecek yeni metodlar

    private final String uploadDir = "uploads/pets/";

    @Override
    @Transactional
    public String uploadPetPhoto(String petId, org.springframework.web.multipart.MultipartFile file) {
        // 1. Pet kontrolü
        Pet pet = petRepository.findByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet bulunamadı: " + petId));

        try {
            // 2. Klasör yoksa oluştur
            java.io.File directory = new java.io.File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            // 3. Benzersiz dosya adı oluştur (Örn: petid_timestamp.jpg)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = petId + "_" + System.currentTimeMillis() + extension;

            // 4. Dosyayı diske kaydet
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + fileName);
            java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 5. Veritabanında URL'i güncelle (Sadece dosya adını saklamak yeterli)
            pet.setPhotoUrl(fileName);
            petRepository.save(pet);

            return fileName;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Dosya yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deletePet(String petId) {
        Pet existing = petRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen pet bulunamadı: " + petId));

        petRepository.delete(existing);
    }
}
