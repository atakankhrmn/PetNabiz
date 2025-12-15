package com.petnabiz.petnabiz.dto.response.appointment;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentResponseDTO {
    private String appointmentId;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String reason;

    private String petId;   // summary yerine şimdilik id dönelim (en garanti)
    private String vetId;
}
