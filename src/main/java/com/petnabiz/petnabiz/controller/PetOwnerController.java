package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.service.PetOwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pet-owners")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    public PetOwnerController(PetOwnerService petOwnerService) {
        this.petOwnerService = petOwnerService;
    }

    /**
     * 1) Tüm pet owner'ları getir
     * GET /api/pet-owners
     */
    @GetMapping
    public ResponseEntity<List<PetOwner>> getAllPetOwners() {
        return ResponseEntity.ok(petOwnerService.getAllPetOwners());
    }

    /**
     * 2) Owner ID'ye göre pet owner getir
     * GET /api/pet-owners/{ownerId}
     */
    @GetMapping("/{ownerId}")
    public ResponseEntity<PetOwner> getPetOwnerById(@PathVariable String ownerId) {
        Optional<PetOwner> ownerOpt = petOwnerService.getPetOwnerById(ownerId);
        return ownerOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3) Email'e göre pet owner getir (opsiyonel, varsa)
     * GET /api/pet-owners/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<PetOwner> getPetOwnerByEmail(@PathVariable String email) {
        Optional<PetOwner> ownerOpt = petOwnerService.getPetOwnerByEmail(email);
        return ownerOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 4) Yeni pet owner oluştur
     * POST /api/pet-owners
     *
     * Body örneği:
     * {
     *   "ownerId": "O001",
     *   "name": "Serhat",
     *   "phone": "555...",
     *   "email": "serhat@example.com"
     * }
     */
    @PostMapping
    public ResponseEntity<PetOwner> createPetOwner(@RequestBody PetOwner petOwner) {
        PetOwner created = petOwnerService.createPetOwner(petOwner);
        return ResponseEntity.ok(created);
    }

    /**
     * 5) Pet owner güncelle
     * PUT /api/pet-owners/{ownerId}
     */
    @PutMapping("/{ownerId}")
    public ResponseEntity<PetOwner> updatePetOwner(
            @PathVariable String ownerId,
            @RequestBody PetOwner updatedPetOwner
    ) {
        PetOwner updated = petOwnerService.updatePetOwner(ownerId, updatedPetOwner);
        return ResponseEntity.ok(updated);
    }

    /**
     * 6) Pet owner sil
     * DELETE /api/pet-owners/{ownerId}
     */
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deletePetOwner(@PathVariable String ownerId) {
        petOwnerService.deletePetOwner(ownerId);
        return ResponseEntity.noContent().build();
    }
}
