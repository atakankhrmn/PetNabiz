package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import com.petnabiz.petnabiz.service.VeterinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLINIC')")
    public ResponseEntity<List<VeterinaryResponseDTO>> getAllVeterinaries() {
        // Eğer CLINIC burada sadece kendi vetlerini görsün istiyorsan
        // bu endpointi kaldırıp sadece /clinic/{clinicId} kullanmak daha temiz.
        return ResponseEntity.ok(veterinaryService.getAllVeterinaries());
    }

    @GetMapping("/{vetId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwnerOfVet(authentication.name, #vetId))")
    public ResponseEntity<VeterinaryResponseDTO> getVeterinaryById(@PathVariable String vetId) {
        return ResponseEntity.ok(veterinaryService.getVeterinaryById(vetId));
    }

    @GetMapping("/clinic/{clinicId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwner(authentication.name, #clinicId))")
    public ResponseEntity<List<VeterinaryResponseDTO>> getVeterinariesByClinicId(@PathVariable String clinicId) {
        return ResponseEntity.ok(veterinaryService.getVeterinariesByClinicId(clinicId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwner(authentication.name, #dto.clinicId))")
    public ResponseEntity<VeterinaryResponseDTO> createVeterinary(@RequestBody VeterinaryCreateRequestDTO dto) {
        return ResponseEntity.ok(veterinaryService.createVeterinary(dto));
    }

    @PutMapping("/{vetId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwnerOfVet(authentication.name, #vetId))")
    public ResponseEntity<VeterinaryResponseDTO> updateVeterinary(
            @PathVariable String vetId,
            @RequestBody VeterinaryUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(veterinaryService.updateVeterinary(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwnerOfVet(authentication.name, #vetId))")
    public ResponseEntity<Void> deleteVeterinary(@PathVariable String vetId) {
        veterinaryService.deleteVeterinary(vetId);
        return ResponseEntity.noContent().build();
    }
}
