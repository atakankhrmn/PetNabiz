package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.appointment.AppointmentCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.appointment.AppointmentUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * 1) Appointment oluştur
     * - OWNER: sadece kendi pet'i için oluşturabilir
     * - ADMIN/CLINIC: oluşturabilir
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @appointmentService.isPetOwnedBy(authentication.name, #dto.petId))")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentCreateRequestDTO dto) {
        AppointmentResponseDTO created = appointmentService.createAppointment(dto);
        URI location = URI.create("/api/appointments/" + created.getAppointmentId()); // DTO field adına göre düzelt
        return ResponseEntity.created(location).body(created);
    }

    /**
     * 2) Tüm appointment'ları getir
     * - ADMIN: hepsini görebilir
     * - CLINIC: kendi kliniğinin hepsini görmeli (genelde service filtreler)
     * - OWNER: BU endpoint'ten hepsini görmemeli -> owner için /my var
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * OWNER için: sadece kendi appointment'ları
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(authentication.getName()));
    }

    /**
     * 3) Appointment ID’ye göre getir
     * - ADMIN/CLINIC: erişebilir
     * - OWNER: sadece kendininkini görebilir
     */
    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @appointmentService.isAppointmentOwnedBy(authentication.name, #appointmentId))")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable String appointmentId) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(appointmentId));
    }

    /**
     * 4) Clinic ID’ye göre appointment getir
     * OWNER'a kapalı (yoksa başka clinic'in randevularını görür)
     */
    @GetMapping("/clinic/{clinicId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByClinicId(@PathVariable String clinicId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByClinicId(clinicId));
    }

    /**
     * 5) Vet ID’ye göre appointment getir
     * OWNER'a kapalı
     */
    @GetMapping("/vet/{vetId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByVetId(@PathVariable String vetId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByVeterinaryId(vetId));
    }

    /**
     * 6) Pet ID’ye göre appointment getir
     * - ADMIN/CLINIC: görebilir
     * - OWNER: sadece kendi pet'i ise görebilir
     */
    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @appointmentService.isPetOwnedBy(authentication.name, #petId))")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPetId(petId));
    }

    /**
     * 7) Appointment güncelle
     * - ADMIN/CLINIC: günceller
     * - OWNER: sadece kendininki
     */
    @PutMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @appointmentService.isAppointmentOwnedBy(authentication.name, #appointmentId))")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable String appointmentId,
            @Valid @RequestBody AppointmentUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, dto));
    }

    /**
     * 8) Appointment iptal et (slot'u tekrar boşaltır)
     * - ADMIN/CLINIC: iptal edebilir
     * - OWNER: sadece kendininki
     */
    @PostMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','CLINIC') or (hasRole('OWNER') and @appointmentService.isAppointmentOwnedBy(authentication.name, #appointmentId))")
    public ResponseEntity<Void> cancelAppointment(@PathVariable String appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }
}
