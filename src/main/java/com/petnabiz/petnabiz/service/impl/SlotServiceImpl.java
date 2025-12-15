package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Slot;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.AppointmentRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.SlotRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.SlotService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;

    public SlotServiceImpl(SlotRepository slotRepository,
                           VeterinaryRepository veterinaryRepository,
                           PetRepository petRepository,
                           AppointmentRepository appointmentRepository) {
        this.slotRepository = slotRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.petRepository = petRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public void createDailySlots(String vetId, LocalDate date) {
        Veterinary vet = veterinaryRepository.findById(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Veterinary not found: " + vetId));

        List<LocalTime> times = List.of(
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                LocalTime.of(17, 0)
        );

        List<Slot> slots = times.stream()
                .map(t -> new Slot(date, t, vet))
                .toList();

        try {
            slotRepository.saveAll(slots);
        } catch (DataIntegrityViolationException e) {
            // unique(vet_id, date, time) yüzünden aynı gün tekrar üretmeye çalışınca buraya düşebilir
            // İstersen burada log atarsın, ben sessiz geçtim.
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Slot> getAvailableSlots(String vetId, LocalDate date) {
        return slotRepository.findByVeterinary_VetIdAndDateAndIsBookedFalse(vetId, date);
    }

    @Override
    @Transactional
    public Appointment bookSlot(Long slotId, String petId) {
        // 1) Atomik rezervasyon (race condition bitiyor)
        int updated = slotRepository.bookSlot(slotId);
        if (updated == 0) {
            throw new IllegalStateException("Slot already booked (slotId=" + slotId + ")");
        }

        // 2) Slot + Pet + Vet çek
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found: " + slotId));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found: " + petId));

        // 3) Appointment oluştur
        Appointment appt = new Appointment();
        appt.setDate(slot.getDate());
        appt.setTime(slot.getTime());
        appt.setStatus("Active"); // siz enum/string ne kullanıyorsanız ona göre ayarla
        appt.setVeterinary(slot.getVeterinary());
        appt.setPet(pet);

        return appointmentRepository.save(appt);
    }
}
