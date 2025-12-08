package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment created = appointmentService.createAppointment(appointment);
        return ResponseEntity.ok(created);
    }

    /**
     * 2) Tüm appointment'ları getir
     * GET /api/appointments
     */
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * 3) Appointment ID’ye göre getir
     * GET /api/appointments/{appointmentId}
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable String appointmentId) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(appointmentId);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 4) Clinic ID’ye göre appointment getir (tüm vet'leri tarar)
     * GET /api/appointments/clinic/{clinicId}
     */
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByClinicId(@PathVariable String clinicId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByClinicId(clinicId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 5) Vet ID’ye göre appointment getir
     * GET /api/appointments/vet/{vetId}
     */
    @GetMapping("/vet/{vetId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByVetId(@PathVariable String vetId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByVeterinaryId(vetId));
    }

    /**
     * 6) Pet ID’ye göre appointment getir
     * GET /api/appointments/pet/{petId}
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPetId(@PathVariable String petId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPetId(petId));
    }

    /**
     * 7) Appointment güncelle
     * PUT /api/appointments/{appointmentId}
     */
    @PutMapping("/{appointmentId}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable String appointmentId,
            @RequestBody Appointment updatedData
    ) {
        Appointment updated = appointmentService.updateAppointment(appointmentId, updatedData);
        return ResponseEntity.ok(updated);
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
