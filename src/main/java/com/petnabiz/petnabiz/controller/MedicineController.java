package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.medicine.MedicineCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicine.MedicineUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicine.MedicineResponseDTO;
import com.petnabiz.petnabiz.service.MedicineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @GetMapping("/{medicineId}")
    public ResponseEntity<MedicineResponseDTO> getMedicineById(@PathVariable String medicineId) {
        return ResponseEntity.ok(medicineService.getMedicineById(medicineId));
    }

    @PostMapping
    public ResponseEntity<MedicineResponseDTO> createMedicine(@RequestBody MedicineCreateRequestDTO dto) {
        return ResponseEntity.ok(medicineService.createMedicine(dto));
    }

    @PutMapping("/{medicineId}")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @PathVariable String medicineId,
            @RequestBody MedicineUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(medicineService.updateMedicine(medicineId, dto));
    }

    @DeleteMapping("/{medicineId}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String medicineId) {
        medicineService.deleteMedicine(medicineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MedicineResponseDTO>> getMedicinesByType(@PathVariable String type) {
        return ResponseEntity.ok(medicineService.getMedicinesByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicineResponseDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(medicineService.searchByName(name));
    }
}
