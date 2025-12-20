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

        String fullName = a.getVeterinary().getFirstName() + " " + a.getVeterinary().getLastName();
        dto.setVetName(a.getVeterinary() != null ? fullName: null);

        String clinicName = a.getVeterinary().getClinic().getName();
        dto.setClinicName(a.getVeterinary() != null ? clinicName: null);

        String petName = a.getPet().getName();
        dto.setPetName(a.getVeterinary() != null ? petName: null);

        String petOwnerName = a.getPet().getOwner().getFirstName() + " " + a.getPet().getOwner().getLastName();
        dto.setPetOwnerName(a.getVeterinary() != null ? petOwnerName: null);
        return dto;
    }
}
