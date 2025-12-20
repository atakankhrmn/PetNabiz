package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import com.petnabiz.petnabiz.service.VeterinaryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Files;

import java.util.List;

@RestController
@RequestMapping("/api/veterinaries")
public class VeterinaryController {

    private final VeterinaryService veterinaryService;

    public VeterinaryController(VeterinaryService veterinaryService) {
        this.veterinaryService = veterinaryService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VeterinaryResponseDTO>> getAllVeterinaries() {
        // Eğer CLINIC burada sadece kendi vetlerini görsün istiyorsan
        // bu endpointi kaldırıp sadece /clinic/{clinicId} kullanmak daha temiz.
        return ResponseEntity.ok(veterinaryService.getAllVeterinaries());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLINIC')")
    public ResponseEntity<List<VeterinaryResponseDTO>> getAllMyVeterinaries() {
        // Eğer CLINIC burada sadece kendi vetlerini görsün istiyorsan
        // bu endpointi kaldırıp sadece /clinic/{clinicId} kullanmak daha temiz.
        return ResponseEntity.ok(veterinaryService.getAllMyVeterinaries());
    }

    @GetMapping("/{vetId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwnerOfVet(authentication.name, #vetId))")
    public ResponseEntity<VeterinaryResponseDTO> getVeterinaryById(@PathVariable String vetId) {
        return ResponseEntity.ok(veterinaryService.getVeterinaryById(vetId));
    }

    @GetMapping("/clinic/{clinicId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwner(authentication.name, #clinicId))")
    public ResponseEntity<List<VeterinaryResponseDTO>> getVeterinariesByClinicId(@PathVariable String clinicId) {
        return ResponseEntity.ok(veterinaryService.getVeterinariesByClinicId(clinicId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Form Data olduğunu belirtiyoruz
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwner(authentication.name, #dto.clinicId))")
    public ResponseEntity<VeterinaryResponseDTO> createVeterinary(
            @ModelAttribute VeterinaryCreateRequestDTO dto, // RequestBody yerine ModelAttribute
            @RequestParam(value = "file", required = true) MultipartFile file // Dosya parametresi
    ) {
        return ResponseEntity.ok(veterinaryService.createVeterinary(dto, file));
    }

    @PutMapping("/{vetId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwnerOfVet(authentication.name, #vetId))")
    public ResponseEntity<VeterinaryResponseDTO> updateVeterinary(
            @PathVariable String vetId,
            @RequestBody VeterinaryUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(veterinaryService.updateVeterinary(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLINIC') and @veterinaryService.isClinicOwnerOfVet(authentication.name, #vetId))")
    public ResponseEntity<Void> deleteVeterinary(@PathVariable String vetId) {
        veterinaryService.deleteVeterinary(vetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{vetId}/certificate")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')") // Sadece yetkililer görebilir
    public ResponseEntity<Resource> getCertificate(@PathVariable String vetId) {

        // 1. Dosya kaynağını servis üzerinden al
        Resource resource = veterinaryService.getCertificateResource(vetId);

        // 2. Dosya tipini (MIME Type) belirlemeye çalış (PDF mi, JPG mi?)
        String contentType = "application/octet-stream"; // Varsayılan
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception ex) {
            // Tip belirlenemezse varsayılan kalır
        }

        // 3. Dosyayı döndür
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // "inline" dosyayı tarayıcıda açar, "attachment" indirmeyi zorlar. Biz görüntülemek istiyoruz.
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
