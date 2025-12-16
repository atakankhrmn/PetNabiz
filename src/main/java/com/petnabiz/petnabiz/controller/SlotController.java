package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.slot.SlotBookRequestDTO;
import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.dto.response.slot.SlotGenerateResponseDTO;
import com.petnabiz.petnabiz.dto.response.slot.SlotResponseDTO;
import com.petnabiz.petnabiz.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/{vetId}/{date}/generate")
    public ResponseEntity<SlotGenerateResponseDTO> generateDailySlots(
            @PathVariable String vetId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        slotService.createDailySlots(vetId, date);

        SlotGenerateResponseDTO res = new SlotGenerateResponseDTO();
        res.setMessage("Slots generated (if not already existing).");
        res.setVetId(vetId);
        res.setDate(date);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/available")
    public ResponseEntity<List<SlotResponseDTO>> getAvailableSlots(
            @RequestParam String vetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(slotService.getAvailableSlots(vetId, date));
    }

    @PostMapping("/{slotId}/book")
    public ResponseEntity<?> bookSlot(
            @PathVariable Long slotId,
            @RequestBody SlotBookRequestDTO req
    ) {
        try {
            AppointmentResponseDTO created = slotService.bookSlot(slotId, req.getPetId());
            return ResponseEntity.ok(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(
                    java.util.Map.of(
                            "error", "SLOT_ALREADY_BOOKED",
                            "message", e.getMessage()
                    )
            );
        }
    }
}
