package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.clinic.ClinicCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.clinic.ClinicUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinic.ClinicResponseDTO;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;
import com.petnabiz.petnabiz.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    // Liste / detay genelde owner da görebilir (login şartı istiyorsan config'te authenticated yaparsın)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<List<ClinicResponseDTO>> getAllClinics() {
        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    @GetMapping("/{clinicId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<ClinicResponseDTO> getClinicById(@PathVariable String clinicId) {
        return ResponseEntity.ok(clinicService.getClinicById(clinicId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<List<ClinicResponseDTO>> searchClinics(@RequestParam String name) {
        return ResponseEntity.ok(clinicService.searchClinicsByName(name));
    }

    /**
     * /by-email kaldırmak daha doğru. Çünkü email ile clinic çekmek bilgi sızdırır.
     * Onun yerine clinic kendi datasını /my ile çeksin.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CLINIC')")
    public ResponseEntity<ClinicResponseDTO> getMyClinic(Authentication authentication) {
        return ResponseEntity.ok(clinicService.getClinicByEmail(authentication.getName()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicResponseDTO> createClinic(@Valid @RequestBody ClinicCreateRequestDTO dto) {
        ClinicResponseDTO created = clinicService.createClinic(dto);
        URI location = URI.create("/api/clinics/" + created.getClinicId()); // DTO field adına göre düzelt
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{clinicId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @clinicService.isClinicSelf(authentication.name, #clinicId))")
    public ResponseEntity<ClinicResponseDTO> updateClinic(
            @PathVariable String clinicId,
            @Valid @RequestBody ClinicUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(clinicService.updateClinic(clinicId, dto));
    }

    @DeleteMapping("/{clinicId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @clinicService.isClinicSelf(authentication.name, #clinicId))")
    public ResponseEntity<Void> deleteClinic(@PathVariable String clinicId) {
        clinicService.deleteClinic(clinicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clinicId}/veterinaries")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC','OWNER')")
    public ResponseEntity<List<VetSummaryDTO>> getVeterinariesByClinic(@PathVariable String clinicId) {
        return ResponseEntity.ok(clinicService.getVeterinariesByClinic(clinicId));
    }
}
