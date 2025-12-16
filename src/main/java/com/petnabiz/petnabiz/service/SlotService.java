package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.dto.response.slot.SlotResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {

    void createDailySlots(String vetId, LocalDate date);

    List<SlotResponseDTO> getAvailableSlots(String vetId, LocalDate date);

    AppointmentResponseDTO bookSlot(Long slotId, String petId);

    // Security helpers (SpEL için)
    boolean isClinicOwnerOfVet(String clinicEmail, String vetId);

    boolean isPetOwnedBy(String ownerEmail, String petId);
}
