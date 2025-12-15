package com.petnabiz.petnabiz.dto.request.appointment;
import lombok.Data;

@Data
public class AppointmentUpdateStatusRequestDTO {
    private String status; // PENDING / APPROVED / CANCELLED / COMPLETED
}

