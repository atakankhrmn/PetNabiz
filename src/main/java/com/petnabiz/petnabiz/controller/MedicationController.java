package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.medication.MedicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medication.MedicationUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import com.petnabiz.petnabiz.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * Tüm medications:
     * - ADMIN/CLINIC görür
     * - OWNER görmez (owner için pet/record bazlı endpoint var)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<List<MedicationResponseDTO>> getAllMedications() {
        return ResponseEntity.ok(medicationService.getAllMedications());
    }

    /**
     * Medication id ile:
     * - ADMIN/CLINIC görür
     * - OWNER sadece kendi record/pet'ine bağlıysa görür
     */
    @GetMapping("/{medicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @medicationService.isMedicationOwnedBy(authentication.name, #medicationId))")
    public ResponseEntity<MedicationResponseDTO> getMedicationById(@PathVariable String medicationId) {
        return ResponseEntity.ok(medicationService.getMedicationById(medicationId));
    }

    /**
     * Pet'e göre:
     * - ADMIN/CLINIC görür
     * - OWNER sadece kendi pet'iyse görür
     */
    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @medicationService.isPetOwnedBy(authentication.name, #petId))")
    public ResponseEntity<List<MedicationResponseDTO>> getMedicationsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(medicationService.getMedicationsByPetId(petId));
    }

    /**
     * Medical record'a göre:
     * - ADMIN/CLINIC görür
     * - OWNER sadece kendi pet'ine ait record ise görür
     */
    @GetMapping("/record/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @medicationService.isRecordOwnedBy(authentication.name, #recordId))")
    public ResponseEntity<List<MedicationResponseDTO>> getMedicationsByRecordId(@PathVariable String recordId) {
        return ResponseEntity.ok(medicationService.getMedicationsByMedicalRecordId(recordId));
    }

    /**
     * Create:
     * - ADMIN/CLINIC
     * - OWNER yasak
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<MedicationResponseDTO> createMedication(@Valid @RequestBody MedicationCreateRequestDTO dto) {
        MedicationResponseDTO created = medicationService.createMedication(dto);
        URI location = URI.create("/api/medications/" + created.getMedicationId()); // field adına göre düzelt
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Update:
     * - ADMIN/CLINIC
     * - OWNER yasak
     */
    @PutMapping("/{medicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<MedicationResponseDTO> updateMedication(
            @PathVariable String medicationId,
            @Valid @RequestBody MedicationUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicationService.updateMedication(medicationId, dto));
    }

    /**
     * Delete:
     * - ADMIN (istersen CLINIC de açarsın)
     */
    @DeleteMapping("/{medicationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedication(@PathVariable String medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.noContent().build();
    }
}
