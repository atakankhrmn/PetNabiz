package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.pet.PetCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.pet.PetUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.request.pet.PetWeightUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;
import com.petnabiz.petnabiz.service.PetService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * ADMIN: tüm petler
     * (Owner'a kapalı, yoksa bütün petleri görür)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PetResponseDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    /**
     * ADMIN: herhangi bir pet
     * OWNER: sadece kendi pet'i
     */
    /**
     * Pet Detaylarını Getir
     */
    @GetMapping("/{petId}")
    // AŞAĞIDAKİ SATIRA "hasRole('CLINIC')" EKLEDİĞİNDEN EMİN OL:
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLINIC') or (hasRole('OWNER') and @petService.isPetOwnedBy(authentication.name, #petId))")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable String petId) {
        return ResponseEntity.ok(petService.getPetById(petId));
    }

    /**
     * OWNER: kendi pet'leri (ownerId path yok, authentication'dan geliyor)
     * ADMIN: isterse kullanabilir (gerekirse açarız)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<List<PetResponseDTO>> getMyPets() {
        return ResponseEntity.ok(petService.getMyPets());
    }

    /**
     * CREATE:
     * - OWNER: sadece kendine pet oluşturur (dto.ownerId'yi ignore ediyoruz)
     * - ADMIN: dto.ownerId zorunlu (başkası adına oluşturabilir)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<PetResponseDTO> createPet(@RequestBody PetCreateRequestDTO dto) {
        PetResponseDTO created = petService.createPet(dto);
        URI location = URI.create("/api/pets/" + created.getPetId()); // PetResponseDTO field adı petId olmalı
        return ResponseEntity.created(location).body(created);
    }

    /**
     * UPDATE:
     * - ADMIN: hepsi
     * - OWNER: sadece kendi pet'i
     */
    @PutMapping("/{petId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('OWNER') and @petService.isPetOwnedBy(authentication.name, #petId))")
    public ResponseEntity<PetResponseDTO> updatePet(
            @PathVariable String petId,
            @RequestBody PetUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(petService.updatePet(petId, dto));
    }

    @PostMapping(value = "/{petId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @petService.isPetOwnedBy(authentication.name, #petId)")
    public ResponseEntity<String> uploadImage(@PathVariable String petId,
                                              @RequestParam("file") MultipartFile file) {
        String fileName = petService.uploadPetPhoto(petId, file);
        return ResponseEntity.ok(fileName);
    }

    @PutMapping("/weight") // URL'den {petId} kısmını kaldırdık
    // #petId yerine #dto.petId kontrolü yapıyoruz ve CLINIC rolünü ekliyoruz:
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLINIC') or (hasRole('OWNER') and @petService.isPetOwnedBy(authentication.name, #dto.petId))")
    public ResponseEntity<PetResponseDTO> updatePetWeight(@RequestBody PetWeightUpdateRequestDTO dto) {
        // Service metodun muhtemelen (String petId, DTO dto) veya sadece (DTO dto) alıyordur.
        // Eğer (id, dto) alıyorsa:
        return ResponseEntity.ok(petService.updatePetWeight(dto.getPetId(), dto));
    }
    /**
     * DELETE:
     * - ADMIN: hepsi
     * - OWNER: sadece kendi pet'i
     */
    @DeleteMapping("/{petId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('OWNER') and @petService.isPetOwnedBy(authentication.name, #petId))")
    public ResponseEntity<Void> deletePet(@PathVariable String petId) {
        petService.deletePet(petId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PetResponseDTO> getByOwnerPhoneAndPetName(
            @RequestParam String phone,
            @RequestParam String petName
    ) {
        PetResponseDTO dto = petService.getPetByPhoneNumberAndPetName(phone, petName);
        return ResponseEntity.ok(dto);
    }
}
