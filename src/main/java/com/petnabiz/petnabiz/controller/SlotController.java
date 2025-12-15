package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Slot;
import com.petnabiz.petnabiz.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    // 1) Günlük slot üret (vet + date için 8 slot)
    @PostMapping("/{vetId}/{date}/generate")
    public ResponseEntity<?> generateDailySlots(
            @PathVariable String vetId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        slotService.createDailySlots(vetId, date);
        return ResponseEntity.ok(Map.of(
                "message", "Slots generated (if not already existing).",
                "vetId", vetId,
                "date", date.toString()
        ));
    }

    // 2) Boş slotları getir
    // Örn: /api/slots/available?vetId=7777&date=2025-12-15
    @GetMapping("/available")
    public ResponseEntity<List<Slot>> getAvailableSlots(
            @RequestParam String vetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(slotService.getAvailableSlots(vetId, date));
    }

    // 3) Slotu rezerve et + appointment oluştur
    // Örn: /api/slots/12/book?petId=3162
    @PostMapping("/{slotId}/book")
    public ResponseEntity<?> bookSlot(
            @PathVariable Long slotId,
            @RequestParam String petId
    ) {
        try {
            Appointment created = slotService.bookSlot(slotId, petId);
            return ResponseEntity.ok(created);
        } catch (IllegalStateException e) {
            // Slot dolu
            return ResponseEntity.status(409).body(Map.of(
                    "error", "SLOT_ALREADY_BOOKED",
                    "message", e.getMessage()
            ));
        }
    }
}
