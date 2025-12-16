package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.medication.MedicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medication.MedicationUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import com.petnabiz.petnabiz.service.MedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public ResponseEntity<List<MedicationResponseDTO>> getAllMedications() {
        return ResponseEntity.ok(medicationService.getAllMedications());
    }

    @GetMapping("/{medicationId}")
    public ResponseEntity<MedicationResponseDTO> getMedicationById(@PathVariable String medicationId) {
        return ResponseEntity.ok(medicationService.getMedicationById(medicationId));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicationResponseDTO>> getMedicationsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(medicationService.getMedicationsByPetId(petId));
    }

    @GetMapping("/record/{recordId}")
    public ResponseEntity<List<MedicationResponseDTO>> getMedicationsByRecordId(@PathVariable String recordId) {
        return ResponseEntity.ok(medicationService.getMedicationsByMedicalRecordId(recordId));
    }

    @PostMapping
    public ResponseEntity<MedicationResponseDTO> createMedication(@RequestBody MedicationCreateRequestDTO dto) {
        return ResponseEntity.ok(medicationService.createMedication(dto));
    }

    @PutMapping("/{medicationId}")
    public ResponseEntity<MedicationResponseDTO> updateMedication(
            @PathVariable String medicationId,
            @RequestBody MedicationUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicationService.updateMedication(medicationId, dto));
    }

    @DeleteMapping("/{medicationId}")
    public ResponseEntity<Void> deleteMedication(@PathVariable String medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.noContent().build();
    }
}
