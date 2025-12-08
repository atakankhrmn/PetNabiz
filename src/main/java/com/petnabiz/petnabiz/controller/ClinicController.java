package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    /**
     * Tüm klinikleri getir
     * GET /api/clinics
     */
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    /**
     * ID'ye göre klinik getir
     * GET /api/clinics/{clinicId}
     */
    @GetMapping("/{clinicId}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable String clinicId) {
        Optional<Clinic> clinicOpt = clinicService.getClinicById(clinicId);
        return clinicOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Yeni klinik oluştur
     * POST /api/clinics
     */
    @PostMapping
    public ResponseEntity<Clinic> createClinic(@RequestBody Clinic clinic) {
        Clinic created = clinicService.createClinic(clinic);
        return ResponseEntity.ok(created);
    }

    /**
     * Klinik güncelle
     * PUT /api/clinics/{clinicId}
     */
    @PutMapping("/{clinicId}")
    public ResponseEntity<Clinic> updateClinic(
            @PathVariable String clinicId,
            @RequestBody Clinic updatedClinic
    ) {
        Clinic updated = clinicService.updateClinic(clinicId, updatedClinic);
        return ResponseEntity.ok(updated);
    }

    /**
     * Klinik sil
     * DELETE /api/clinics/{clinicId}
     */
    @DeleteMapping("/{clinicId}")
    public ResponseEntity<Void> deleteClinic(@PathVariable String clinicId) {
        clinicService.deleteClinic(clinicId);
        return ResponseEntity.noContent().build();
    }
}
