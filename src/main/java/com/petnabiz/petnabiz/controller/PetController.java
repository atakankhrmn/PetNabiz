package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * 1) Tüm pet'leri getir
     * GET /api/pets
     */
    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    /**
     * 2) Pet ID'ye göre pet getir
     * GET /api/pets/{petId}
     */
    @GetMapping("/{petId}")
    public ResponseEntity<Pet> getPetById(@PathVariable String petId) {
        Optional<Pet> petOpt = petService.getPetById(petId);
        return petOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3) Owner ID'ye göre pet'leri getir
     * GET /api/pets/owner/{ownerId}
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Pet>> getPetsByOwnerId(@PathVariable String ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwnerId(ownerId));
    }

    /**
     * 4) Clinic ID'ye göre pet'leri getir (kliniğe kayıtlı hastalar)
     * GET /api/pets/clinic/{clinicId}
     */
    /*
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<Pet>> getPetsByClinicId(@PathVariable String clinicId) {
        return ResponseEntity.ok(petService.getPetsByClinicId(clinicId));
    }
    */

    /**
     * 5) Yeni pet oluştur
     * POST /api/pets
     *
     * Body örneği:
     * {
     *   "petId": "P001",
     *   "name": "Pamuk",
     *   "species": "Köpek",
     *   "breed": "Golden",
     *   "owner": { "ownerId": "O001" }
     * }
     */
    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        Pet created = petService.createPet(pet);
        return ResponseEntity.ok(created);
    }

    /**
     * 6) Pet bilgisi güncelle
     * PUT /api/pets/{petId}
     */
    @PutMapping("/{petId}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable String petId,
            @RequestBody Pet updatedPet
    ) {
        Pet updated = petService.updatePet(petId, updatedPet);
        return ResponseEntity.ok(updated);
    }

    /**
     * 7) Pet sil
     * DELETE /api/pets/{petId}
     */
    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable String petId) {
        petService.deletePet(petId);
        return ResponseEntity.noContent().build();
    }
}
