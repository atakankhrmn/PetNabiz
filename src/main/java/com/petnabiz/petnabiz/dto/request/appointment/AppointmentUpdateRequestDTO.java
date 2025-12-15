package com.petnabiz.petnabiz.dto.request.appointment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentUpdateRequestDTO {
    private String petId;           // pet değiştirmek istersen
    private String vetId;           // vet değiştirmek istersen
    private LocalDateTime dateTime; // tarih/saat güncellemek istersen
    private String status;
    private String reason;
}
