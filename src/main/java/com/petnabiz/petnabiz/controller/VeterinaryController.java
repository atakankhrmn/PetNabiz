package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.service.VeterinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/veterinaries")
public class VeterinaryController {

    private final VeterinaryService veterinaryService;

    public VeterinaryController(VeterinaryService veterinaryService) {
        this.veterinaryService = veterinaryService;
    }

    /**
     * 1) Tüm veterinerleri getir
     * GET /api/veterinaries
     */
    @GetMapping
    public ResponseEntity<List<Veterinary>> getAllVeterinaries() {
        return ResponseEntity.ok(veterinaryService.getAllVeterinaries());
    }

    /**
     * 2) Vet ID'ye göre veteriner getir
     * GET /api/veterinaries/{vetId}
     */
    @GetMapping("/{vetId}")
    public ResponseEntity<Veterinary> getVeterinaryById(@PathVariable String vetId) {
        Optional<Veterinary> vetOpt = veterinaryService.getVeterinaryById(vetId);
        return vetOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3) Clinic ID'ye göre veterinerleri getir
     * GET /api/veterinaries/clinic/{clinicId}
     */
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<Veterinary>> getVeterinariesByClinicId(@PathVariable String clinicId) {
        return ResponseEntity.ok(veterinaryService.getVeterinariesByClinicId(clinicId));
    }

    /**
     * 5) Yeni veteriner oluştur
     * POST /api/veterinaries
     *
     * Body örneği:
     * {
     *   "vetId": "V001",
     *   "name": "Dr. Ayşe",
     *   "specialization": "Dermatology",
     *   "clinic": { "clinicId": "C001" }
     * }
     */
    @PostMapping
    public ResponseEntity<Veterinary> createVeterinary(@RequestBody Veterinary veterinary) {
        Veterinary created = veterinaryService.createVeterinary(veterinary);
        return ResponseEntity.ok(created);
    }

    /**
     * 6) Veteriner bilgisi güncelle
     * PUT /api/veterinaries/{vetId}
     */
    @PutMapping("/{vetId}")
    public ResponseEntity<Veterinary> updateVeterinary(
            @PathVariable String vetId,
            @RequestBody Veterinary updatedVeterinary
    ) {
        Veterinary updated = veterinaryService.updateVeterinary(vetId, updatedVeterinary);
        return ResponseEntity.ok(updated);
    }

    /**
     * 7) Veteriner sil
     * DELETE /api/veterinaries/{vetId}
     */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVeterinary(@PathVariable String vetId) {
        veterinaryService.deleteVeterinary(vetId);
        return ResponseEntity.noContent().build();
    }
}
