package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Medication;
import com.petnabiz.petnabiz.service.MedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * 1) Tüm medication'ları getir
     * GET /api/medications
     */
    @GetMapping
    public ResponseEntity<List<Medication>> getAllMedications() {
        return ResponseEntity.ok(medicationService.getAllMedications());
    }

    /**
     * 2) ID'ye göre medication getir
     * GET /api/medications/{medicationId}
     */
    @GetMapping("/{medicationId}")
    public ResponseEntity<Medication> getMedicationById(@PathVariable String medicationId) {
        Optional<Medication> medOpt = medicationService.getMedicationById(medicationId);
        return medOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3) Pet ID'ye göre medication'ları getir
     * GET /api/medications/pet/{petId}
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<Medication>> getMedicationsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(medicationService.getMedicationsByPetId(petId));
    }

    /**
     * 4) Medical Record ID'ye göre medication'ları getir (eğer ilişki varsa)
     * GET /api/medications/record/{recordId}
     */
    @GetMapping("/record/{recordId}")
    public ResponseEntity<List<Medication>> getMedicationsByRecordId(@PathVariable String recordId) {
        return ResponseEntity.ok(medicationService.getMedicationsByMedicalRecordId(recordId));
    }

    /**
     * 5) Yeni medication oluştur
     * POST /api/medications
     *
     * Body örneği:
     * {
     *   "pet": { "petId": "P001" },
     *   "medicalRecord": { "recordId": "R100" },
     *   "name": "Antibiyotik",
     *   "dosage": "Günde 2 kez",
     *   "startDate": "2025-02-10",
     *   "endDate": "2025-02-17"
     * }
     */
    @PostMapping
    public ResponseEntity<Medication> createMedication(@RequestBody Medication medication) {
        Medication created = medicationService.createMedication(medication);
        return ResponseEntity.ok(created);
    }

    /**
     * 6) Medication güncelle
     * PUT /api/medications/{medicationId}
     */
    @PutMapping("/{medicationId}")
    public ResponseEntity<Medication> updateMedication(
            @PathVariable String medicationId,
            @RequestBody Medication updatedMedication
    ) {
        Medication updated = medicationService.updateMedication(medicationId, updatedMedication);
        return ResponseEntity.ok(updated);
    }

    /**
     * 7) Medication sil
     * DELETE /api/medications/{medicationId}
     */
    @DeleteMapping("/{medicationId}")
    public ResponseEntity<Void> deleteMedication(@PathVariable String medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.noContent().build();
    }
}
