package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.pet.PetCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.pet.PetUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;
import com.petnabiz.petnabiz.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable String petId) {
        return ResponseEntity.ok(petService.getPetById(petId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetResponseDTO>> getPetsByOwnerId(@PathVariable String ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwnerId(ownerId));
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> createPet(@RequestBody PetCreateRequestDTO dto) {
        return ResponseEntity.ok(petService.createPet(dto));
    }

    @PutMapping("/{petId}")
    public ResponseEntity<PetResponseDTO> updatePet(
            @PathVariable String petId,
            @RequestBody PetUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(petService.updatePet(petId, dto));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable String petId) {
        petService.deletePet(petId);
        return ResponseEntity.noContent().build();
    }
}
