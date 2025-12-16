package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import com.petnabiz.petnabiz.service.VeterinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinaries")
public class VeterinaryController {

    private final VeterinaryService veterinaryService;

    public VeterinaryController(VeterinaryService veterinaryService) {
        this.veterinaryService = veterinaryService;
    }

    @GetMapping
    public ResponseEntity<List<VeterinaryResponseDTO>> getAllVeterinaries() {
        return ResponseEntity.ok(veterinaryService.getAllVeterinaries());
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VeterinaryResponseDTO> getVeterinaryById(@PathVariable String vetId) {
        return ResponseEntity.ok(veterinaryService.getVeterinaryById(vetId));
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<VeterinaryResponseDTO>> getVeterinariesByClinicId(@PathVariable String clinicId) {
        return ResponseEntity.ok(veterinaryService.getVeterinariesByClinicId(clinicId));
    }

    @PostMapping
    public ResponseEntity<VeterinaryResponseDTO> createVeterinary(@RequestBody VeterinaryCreateRequestDTO dto) {
        return ResponseEntity.ok(veterinaryService.createVeterinary(dto));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VeterinaryResponseDTO> updateVeterinary(
            @PathVariable String vetId,
            @RequestBody VeterinaryUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(veterinaryService.updateVeterinary(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVeterinary(@PathVariable String vetId) {
        veterinaryService.deleteVeterinary(vetId);
        return ResponseEntity.noContent().build();
    }
}
