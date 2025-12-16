package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.medicine.MedicineCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicine.MedicineUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicine.MedicineResponseDTO;
import com.petnabiz.petnabiz.service.MedicineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    // Read: login olan herkes (admin/clinic/owner)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @GetMapping("/{medicineId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<MedicineResponseDTO> getMedicineById(@PathVariable String medicineId) {
        return ResponseEntity.ok(medicineService.getMedicineById(medicineId));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<List<MedicineResponseDTO>> getMedicinesByType(@PathVariable String type) {
        return ResponseEntity.ok(medicineService.getMedicinesByType(type));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<List<MedicineResponseDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(medicineService.searchByName(name));
    }

    // Write: admin + clinic
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<MedicineResponseDTO> createMedicine(@RequestBody MedicineCreateRequestDTO dto) {
        return ResponseEntity.ok(medicineService.createMedicine(dto));
    }

    @PutMapping("/{medicineId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @PathVariable String medicineId,
            @RequestBody MedicineUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicineService.updateMedicine(medicineId, dto));
    }

    // Delete: sadece admin
    @DeleteMapping("/{medicineId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String medicineId) {
        medicineService.deleteMedicine(medicineId);
        return ResponseEntity.noContent().build();
    }
}
