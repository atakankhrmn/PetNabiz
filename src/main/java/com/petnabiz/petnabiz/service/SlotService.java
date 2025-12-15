package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Slot;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {

    void createDailySlots(String vetId, LocalDate date);

    List<Slot> getAvailableSlots(String vetId, LocalDate date);

    Appointment bookSlot(Long slotId, String petId);
}
