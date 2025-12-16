package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.clinic.ClinicCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.clinic.ClinicUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinic.ClinicResponseDTO;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;
import com.petnabiz.petnabiz.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponseDTO>> getAllClinics() {
        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    @GetMapping("/{clinicId}")
    public ResponseEntity<ClinicResponseDTO> getClinicById(@PathVariable String clinicId) {
        return ResponseEntity.ok(clinicService.getClinicById(clinicId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClinicResponseDTO>> searchClinics(@RequestParam String name) {
        return ResponseEntity.ok(clinicService.searchClinicsByName(name));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ClinicResponseDTO> getClinicByEmail(@RequestParam String email) {
        return ResponseEntity.ok(clinicService.getClinicByEmail(email));
    }

    @PostMapping
    public ResponseEntity<ClinicResponseDTO> createClinic(@RequestBody ClinicCreateRequestDTO dto) {
        return ResponseEntity.ok(clinicService.createClinic(dto));
    }

    @PutMapping("/{clinicId}")
    public ResponseEntity<ClinicResponseDTO> updateClinic(
            @PathVariable String clinicId,
            @RequestBody ClinicUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(clinicService.updateClinic(clinicId, dto));
    }

    @DeleteMapping("/{clinicId}")
    public ResponseEntity<Void> deleteClinic(@PathVariable String clinicId) {
        clinicService.deleteClinic(clinicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clinicId}/veterinaries")
    public ResponseEntity<List<VetSummaryDTO>> getVeterinariesByClinic(@PathVariable String clinicId) {
        return ResponseEntity.ok(clinicService.getVeterinariesByClinic(clinicId));
    }
}
