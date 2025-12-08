package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Medicine;
import com.petnabiz.petnabiz.service.MedicineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    /**
     * Tüm ilaçları getir
     * GET /api/medicines
     */
    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    /**
     * ID'ye göre ilaç getir
     * GET /api/medicines/{medicineId}
     */
    @GetMapping("/{medicineId}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable String medicineId) {
        Optional<Medicine> medOpt = medicineService.getMedicineById(medicineId);
        return medOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Yeni ilaç ekle (kataloğa)
     * POST /api/medicines
     */
    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        Medicine created = medicineService.createMedicine(medicine);
        return ResponseEntity.ok(created);
    }

    /**
     * İlaç bilgisi güncelle
     * PUT /api/medicines/{medicineId}
     */
    @PutMapping("/{medicineId}")
    public ResponseEntity<Medicine> updateMedicine(
            @PathVariable String medicineId,
            @RequestBody Medicine updatedMedicine
    ) {
        Medicine updated = medicineService.updateMedicine(medicineId, updatedMedicine);
        return ResponseEntity.ok(updated);
    }

    /**
     * İlaç sil
     * DELETE /api/medicines/{medicineId}
     */
    @DeleteMapping("/{medicineId}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String medicineId) {
        medicineService.deleteMedicine(medicineId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/medicines/type/{type}
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Medicine>> getMedicinesByType(@PathVariable String type) {
        return ResponseEntity.ok(medicineService.getMedicinesByType(type));
    }

}
