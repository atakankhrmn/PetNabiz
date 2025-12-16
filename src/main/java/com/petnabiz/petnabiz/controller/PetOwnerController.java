package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.petowner.PetOwnerUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.petowner.PetOwnerResponseDTO;
import com.petnabiz.petnabiz.service.PetOwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet-owners")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    public PetOwnerController(PetOwnerService petOwnerService) {
        this.petOwnerService = petOwnerService;
    }

    @GetMapping
    public ResponseEntity<List<PetOwnerResponseDTO>> getAllPetOwners() {
        return ResponseEntity.ok(petOwnerService.getAllPetOwners());
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<PetOwnerResponseDTO> getPetOwnerById(@PathVariable String ownerId) {
        return ResponseEntity.ok(petOwnerService.getPetOwnerById(ownerId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PetOwnerResponseDTO> getPetOwnerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(petOwnerService.getPetOwnerByEmail(email));
    }

    @PostMapping
    public ResponseEntity<PetOwnerResponseDTO> createPetOwner(@RequestBody PetOwnerCreateRequestDTO dto) {
        return ResponseEntity.ok(petOwnerService.createPetOwner(dto));
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<PetOwnerResponseDTO> updatePetOwner(
            @PathVariable String ownerId,
            @RequestBody PetOwnerUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(petOwnerService.updatePetOwner(ownerId, dto));
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deletePetOwner(@PathVariable String ownerId) {
        petOwnerService.deletePetOwner(ownerId);
        return ResponseEntity.noContent().build();
    }
}
