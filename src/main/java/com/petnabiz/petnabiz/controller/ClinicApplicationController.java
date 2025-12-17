package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.clinicapplication.ClinicApplicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinicapplication.ClinicApplicationResponseDTO;
import com.petnabiz.petnabiz.model.ApplicationStatus;
import com.petnabiz.petnabiz.service.ClinicApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clinic-applications")
public class ClinicApplicationController {

    private final ClinicApplicationService clinicApplicationService;

    public ClinicApplicationController(ClinicApplicationService clinicApplicationService) {
        this.clinicApplicationService = clinicApplicationService;
    }

    // PUBLIC: başvuru oluştur
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> createApplication(
            @ModelAttribute ClinicApplicationCreateRequestDTO dto,
            @RequestPart("document") MultipartFile document
    ) {
        Long id = clinicApplicationService.createApplication(dto, document);
        return ResponseEntity.created(URI.create("/api/clinic-applications/" + id)).build();
    }

    // ADMIN: listele
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClinicApplicationResponseDTO>> list(
            @RequestParam(defaultValue = "PENDING") ApplicationStatus status
    ) {
        return ResponseEntity.ok(clinicApplicationService.listByStatus(status));
    }

    // ADMIN: approve
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approve(@PathVariable Long id, Authentication auth) {
        clinicApplicationService.approve(id, auth.getName());
        return ResponseEntity.ok().build();
    }

    // ADMIN: reject
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            Authentication auth
    ) {
        clinicApplicationService.reject(id, auth.getName());
        return ResponseEntity.ok().build();
    }
}
