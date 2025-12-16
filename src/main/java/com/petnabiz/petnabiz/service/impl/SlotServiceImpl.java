package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.dto.response.slot.SlotResponseDTO;
import com.petnabiz.petnabiz.mapper.AppointmentMapper;
import com.petnabiz.petnabiz.mapper.SlotMapper;
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

@Service("slotService") // SpEL için
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final SlotMapper slotMapper;
    private final AppointmentMapper appointmentMapper;

    public SlotServiceImpl(SlotRepository slotRepository,
                           VeterinaryRepository veterinaryRepository,
                           PetRepository petRepository,
                           AppointmentRepository appointmentRepository,
                           SlotMapper slotMapper,
                           AppointmentMapper appointmentMapper) {
        this.slotRepository = slotRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.petRepository = petRepository;
        this.appointmentRepository = appointmentRepository;
        this.slotMapper = slotMapper;
        this.appointmentMapper = appointmentMapper;
    }

    // ----------------------------
    // Security helpers
    // ----------------------------
    @Override
    public boolean isClinicOwnerOfVet(String clinicEmail, String vetId) {
        if (clinicEmail == null || clinicEmail.isBlank() || vetId == null || vetId.isBlank()) return false;

        Veterinary vet = veterinaryRepository.findByVetId(vetId).orElse(null);
        if (vet == null || vet.getClinic() == null || vet.getClinic().getUser() == null) return false;

        String email = vet.getClinic().getUser().getEmail();
        return email != null && clinicEmail.equalsIgnoreCase(email);
    }

    @Override
    public boolean isPetOwnedBy(String ownerEmail, String petId) {
        if (ownerEmail == null || ownerEmail.isBlank() || petId == null || petId.isBlank()) return false;

        Pet pet = petRepository.findByPetId(petId).orElse(null);
        if (pet == null || pet.getOwner() == null || pet.getOwner().getUser() == null) return false;

        String email = pet.getOwner().getUser().getEmail();
        return email != null && ownerEmail.equalsIgnoreCase(email);
    }

    // ----------------------------
    // Business
    // ----------------------------
    @Override
    @Transactional
    public void createDailySlots(String vetId, LocalDate date) {

        Veterinary vet = veterinaryRepository.findByVetId(vetId)
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
            // unique(vet_id, date, time) -> tekrar generate edilirse patlar, sessiz geçiyoruz
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponseDTO> getAvailableSlots(String vetId, LocalDate date) {
        return slotRepository.findByVeterinary_VetIdAndDateAndIsBookedFalse(vetId, date)
                .stream()
                .map(slotMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponseDTO bookSlot(Long slotId, String petId) {

        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("petId zorunlu.");
        }

        // 1) Atomik book (race condition yok)
        int updated = slotRepository.bookSlot(slotId);
        if (updated == 0) {
            throw new IllegalStateException("Slot already booked (slotId=" + slotId + ")");
        }

        // 2) Slot + Pet load
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found: " + slotId));

        Pet pet = petRepository.findByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found: " + petId));

        // 3) Appointment create
        Appointment appt = new Appointment();
        appt.setDate(slot.getDate());
        appt.setTime(slot.getTime());
        appt.setStatus("Active");
        appt.setVeterinary(slot.getVeterinary());
        appt.setPet(pet);

        Appointment saved = appointmentRepository.save(appt);
        return appointmentMapper.toResponse(saved);
    }
}
