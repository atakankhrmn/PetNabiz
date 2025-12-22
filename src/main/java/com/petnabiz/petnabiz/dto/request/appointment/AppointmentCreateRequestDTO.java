package com.petnabiz.petnabiz.dto.request.appointment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCreateRequestDTO {
    private String appointmentId; // opsiyonel: client gönderecekse
    private String petId;         // opsiyonel (entity nullable)
    private String vetId;         // zorunlu
    private LocalDateTime dateTime; // zorunlu
    private String reason;        // opsiyonel
}
