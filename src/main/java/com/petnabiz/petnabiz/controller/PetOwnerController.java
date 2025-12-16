package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.petowner.PetOwnerResponseDTO;
import com.petnabiz.petnabiz.service.PetOwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pet-owners")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    public PetOwnerController(PetOwnerService petOwnerService) {
        this.petOwnerService = petOwnerService;
    }

    // ADMIN: liste
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PetOwnerResponseDTO>> getAllPetOwners() {
        return ResponseEntity.ok(petOwnerService.getAllPetOwners());
    }

    // OWNER: kendi profilini buradan al
    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PetOwnerResponseDTO> getMyProfile() {
        return ResponseEntity.ok(petOwnerService.getMyProfile());
    }

    // ADMIN veya OWNER(self) -> id ile get
    @GetMapping("/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('OWNER') and @petOwnerService.isSelf(#ownerId, authentication.name))")
    public ResponseEntity<PetOwnerResponseDTO> getPetOwnerById(@PathVariable String ownerId) {
        return ResponseEntity.ok(petOwnerService.getPetOwnerById(ownerId));
    }

    // Email ile get: privacy, sadece ADMIN
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetOwnerResponseDTO> getPetOwnerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(petOwnerService.getPetOwnerByEmail(email));
    }

    /**
     * Register endpoint
     * - bunu permitAll yapacaksan SecurityConfig’te /api/pet-owners POST'u permitAll aç.
     * - Açmak istemiyorsan @PreAuthorize'ı admin yap.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // istersen permitAll yaparız
    public ResponseEntity<PetOwnerResponseDTO> createPetOwner(@RequestBody PetOwnerCreateRequestDTO dto) {
        PetOwnerResponseDTO created = petOwnerService.createPetOwner(dto);
        URI location = URI.create("/api/pet-owners/" + created.getOwnerId()); // DTO field adı ownerId olmalı
        return ResponseEntity.created(location).body(created);
    }

    // ADMIN veya OWNER(self) update
    @PutMapping("/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('OWNER') and @petOwnerService.isSelf(#ownerId, authentication.name))")
    public ResponseEntity<PetOwnerResponseDTO> updatePetOwner(
            @PathVariable String ownerId,
            @RequestBody PetOwnerUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(petOwnerService.updatePetOwner(ownerId, dto));
    }

    // Delete sadece ADMIN (self-delete istiyorsan ayrıca açarız)
    @DeleteMapping("/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePetOwner(@PathVariable String ownerId) {
        petOwnerService.deletePetOwner(ownerId);
        return ResponseEntity.noContent().build();
    }
}
