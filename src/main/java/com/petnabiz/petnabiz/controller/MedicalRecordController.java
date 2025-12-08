package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * 1) Tüm medical record'ları getir
     * GET /api/medical-records
     */
    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    /**
     * 2) ID'ye göre medical record getir
     * GET /api/medical-records/{recordId}
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable String recordId) {
        Optional<MedicalRecord> recordOpt = medicalRecordService.getMedicalRecordById(recordId);
        return recordOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3) Pet ID'ye göre medical record'ları getir
     * GET /api/medical-records/pet/{petId}
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByPetId(petId));
    }

    /**
     * 4) Vet ID'ye göre medical record'ları getir (istersen)
     * GET /api/medical-records/vet/{vetId}
     */
    @GetMapping("/vet/{vetId}")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByVetId(@PathVariable String vetId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByVeterinaryId(vetId));
    }

    /**
     * 5) Yeni medical record oluştur
     * POST /api/medical-records
     *
     * Body örneği:
     * {
     *   "pet": { "petId": "P001" },
     *   "veterinary": { "vetId": "V001" },
     *   "diagnosis": "Kronik gastrit",
     *   "treatment": "Özel diyet + ilaç",
     *   "recordDate": "2025-02-10"
     * }
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        MedicalRecord created = medicalRecordService.createMedicalRecord(medicalRecord);
        return ResponseEntity.ok(created);
    }

    /**
     * 6) Medical record güncelle
     * PUT /api/medical-records/{recordId}
     */
    @PutMapping("/{recordId}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(
            @PathVariable String recordId,
            @RequestBody MedicalRecord updatedRecord
    ) {
        MedicalRecord updated = medicalRecordService.updateMedicalRecord(recordId, updatedRecord);
        return ResponseEntity.ok(updated);
    }

    /**
     * 7) Medical record sil
     * DELETE /api/medical-records/{recordId}
     */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}
