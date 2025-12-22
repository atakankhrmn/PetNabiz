package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.dto.response.slot.SlotResponseDTO;
import com.petnabiz.petnabiz.mapper.AppointmentMapper;
import com.petnabiz.petnabiz.mapper.SlotMapper;
import com.petnabiz.petnabiz.model.*;
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
import java.util.UUID;

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
    @Transactional(readOnly = true)
    public List<SlotResponseDTO> getAllSlots(String vetId, LocalDate date) {
        return slotRepository.findByVeterinary_VetIdAndDate(vetId, date)
                .stream()
                .map(slotMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteSlot(Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot bulunamadı id: " + slotId));

        // ÖNEMLİ KONTROL: Eğer slot doluysa silinmesine izin verme (veya önce randevuyu iptal ettir)
        if (slot.isBooked()) {
            throw new IllegalStateException("Bu slot dolu (randevulu) olduğu için silinemez. Önce randevuyu iptal ediniz.");
        }

        slotRepository.delete(slot);
    }

    @Override
    @Transactional
    // 2) Güvenlik Kontrolü (Controller'daki @PreAuthorize için)
    public boolean isClinicOwnerOfSlot(String email, Long slotId) {
        // Slotu bul
        Slot slot = slotRepository.findById(slotId).orElse(null);
        if (slot == null) return false;

        // Slotun sahibi olan veterineri bul
        Veterinary vet = slot.getVeterinary();
        if (vet == null || vet.getClinic() == null) return false;

        // Veterinerin bağlı olduğu kliniği bul
        Clinic clinic = vet.getClinic();

        // Giriş yapan kullanıcının (email) bu kliniğin sahibi olup olmadığına bak
        // User tablosunda clinic kullanıcısının email'i ile clinic tablosundaki email eşleşmeli
        // Veya ClinicService üzerinden bir kontrol çağırabilirsin.
        // Basitçe şöyle varsayıyorum: Clinic tablosunda user_id ile ilişki var.

        // EĞER Clinic entity'sinde 'email' alanı varsa:
        // return clinic.getEmail().equals(email);

        // EĞER User tablosundan gidiyorsak (Senin yapına göre):
        return clinic.getUser().getEmail().equals(email);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO bookSlot(Long slotId, String petId,String reason) {

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
        appt.setVeterinary(slot.getVeterinary());
        appt.setPet(pet);
        appt.setAppointmentId(genAppointmentId());
        appt.setReason(reason);
        appt.setSlot(slotId);

        Appointment saved = appointmentRepository.save(appt);
        return appointmentMapper.toResponse(saved);
    }

    private String genAppointmentId() {
        return "APT" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponseDTO> getAvailableSlotsByDateRangeCityDistrict(
            LocalDate startDate,
            LocalDate endDate,
            String city,
            String district
    ) {
        if (startDate == null || endDate == null || city == null || district == null) {
            throw new IllegalArgumentException("Parametreler boş olamaz");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate startDate'ten önce olamaz");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 🔥 KRİTİK KISIM
        if (startDate.isBefore(today)) {
            startDate = today;
        }

        return slotRepository
                .findAvailableSlotsByDateRangeCityDistrict(
                        startDate,
                        endDate,
                        city.trim(),
                        district.trim(),
                        today,
                        now
                )
                .stream()
                .map(slotMapper::toResponse)
                .toList();
    }


}
