package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;
import com.petnabiz.petnabiz.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Tüm kayıtlar:
     * - ADMIN/CLINIC görür
     * - OWNER görmez (owner için pet bazlı endpoint var)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllMedicalRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    /**
     * RecordId ile getir:
     * - ADMIN/CLINIC görür
     * - OWNER sadece kendi pet'ine aitse görür
     */
    @GetMapping("/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @medicalRecordService.isRecordOwnedBy(authentication.name, #recordId))")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordById(@PathVariable String recordId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(recordId));
    }

    /**
     * PetId ile getir:
     * - ADMIN/CLINIC görür
     * - OWNER sadece kendi pet'iyse görür
     */
    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @medicalRecordService.isPetOwnedBy(authentication.name, #petId))")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByPetId(petId));
    }

    /**
     * VetId ile getir:
     * - OWNER'a KESİN KAPALI (yoksa vetId ile başka kayıtları görür)
     */
    @GetMapping("/vet/{vetId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsByVetId(@PathVariable String vetId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByVeterinaryId(vetId));
    }

    /**
     * Create:
     * - ADMIN/CLINIC
     * - OWNER YASAK
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(@Valid @RequestBody MedicalRecordCreateRequestDTO dto) {
        MedicalRecordResponseDTO created = medicalRecordService.createMedicalRecord(dto);
        URI location = URI.create("/api/medical-records/" + created.getRecordId()); // field adına göre düzelt
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Update:
     * - ADMIN/CLINIC
     * - OWNER YASAK
     */
    @PutMapping("/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecord(
            @PathVariable String recordId,
            @Valid @RequestBody MedicalRecordUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(recordId, dto));
    }

    /**
     * Delete:
     * - ADMIN
     * - CLINIC (iş kuralına göre açılabilir, şimdilik ADMIN'e bıraktım)
     */
    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}
