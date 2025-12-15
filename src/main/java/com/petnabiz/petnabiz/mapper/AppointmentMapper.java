package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponseDTO toResponse(Appointment a) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setAppointmentId(a.getAppointmentId());
        dto.setDate(a.getDate());
        dto.setTime(a.getTime());
        dto.setStatus(a.getStatus());
        dto.setReason(a.getReason());

        dto.setPetId(a.getPet() != null ? a.getPet().getPetId() : null);
        dto.setVetId(a.getVeterinary() != null ? a.getVeterinary().getVetId() : null);

        return dto;
    }
}
