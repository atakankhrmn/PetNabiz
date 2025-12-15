package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.appointment.AppointmentCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.appointment.AppointmentUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * POST /api/appointments
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @RequestBody AppointmentCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(appointmentService.createAppointment(dto));
    }

    /**
     * 2) Tüm appointment'ları getir
     * GET /api/appointments
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * 3) Appointment ID’ye göre getir
     * GET /api/appointments/{appointmentId}
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(
            @PathVariable String appointmentId
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(appointmentId));
    }

    /**
     * 4) Clinic ID’ye göre appointment getir
     * GET /api/appointments/clinic/{clinicId}
     */
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByClinicId(
            @PathVariable String clinicId
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByClinicId(clinicId));
    }

    /**
     * 5) Vet ID’ye göre appointment getir
     * GET /api/appointments/vet/{vetId}
     */
    @GetMapping("/vet/{vetId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByVetId(
            @PathVariable String vetId
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByVeterinaryId(vetId));
    }

    /**
     * 6) Pet ID’ye göre appointment getir
     * GET /api/appointments/pet/{petId}
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPetId(
            @PathVariable String petId
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPetId(petId));
    }

    /**
     * 7) Appointment güncelle
     * PUT /api/appointments/{appointmentId}
     */
    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable String appointmentId,
            @RequestBody AppointmentUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, dto));
    }

    /**
     * 8) Appointment sil
     * DELETE /api/appointments/{appointmentId}
     */
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }
}
