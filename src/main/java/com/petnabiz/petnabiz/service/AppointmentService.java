package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.appointment.AppointmentCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.appointment.AppointmentUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {

    AppointmentResponseDTO createAppointment(AppointmentCreateRequestDTO dto);

    List<AppointmentResponseDTO> getAllAppointments();

    AppointmentResponseDTO getAppointmentById(String appointmentId);

    List<AppointmentResponseDTO> getAppointmentsByPetId(String petId);

    List<AppointmentResponseDTO> getAppointmentsByVeterinaryId(String vetId);

    List<AppointmentResponseDTO> getAppointmentsByClinicId(String clinicId);

    AppointmentResponseDTO updateAppointment(String appointmentId, AppointmentUpdateRequestDTO dto);

    void deleteAppointment(String appointmentId);
}
