package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;
import com.petnabiz.petnabiz.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllMedicalRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordById(@PathVariable String recordId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(recordId));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByPetId(petId));
    }

    @GetMapping("/vet/{vetId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsByVetId(@PathVariable String vetId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByVeterinaryId(vetId));
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(
            @RequestBody MedicalRecordCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicalRecordService.createMedicalRecord(dto));
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecord(
            @PathVariable String recordId,
            @RequestBody MedicalRecordUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(recordId, dto));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}
