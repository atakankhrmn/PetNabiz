package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.appointment.AppointmentCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.appointment.AppointmentUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.mapper.AppointmentMapper;
import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Slot;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.AppointmentRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.SlotRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("appointmentService") // @PreAuthorize içindeki @appointmentService için net bean adı
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final SlotRepository slotRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PetRepository petRepository, SlotRepository slotRepository,
                                  VeterinaryRepository veterinaryRepository,
                                  AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.slotRepository = slotRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.appointmentMapper = appointmentMapper;
    }

    // ---------------------------
    // Security helpers (SpEL için)
    // ---------------------------

    @Override
    public boolean isPetOwnedBy(String ownerEmail, String petId) {
        if (ownerEmail == null || petId == null || petId.isBlank()) return false;

        Pet pet = petRepository.findByPetId(petId).orElse(null);
        if (pet == null) return false;

        // TODO: burada alan adları sizde farklı olabilir:
        // pet.getOwner().getEmail() yerine pet.getPetOwner().getUser().getEmail() vs.
        if (pet.getOwner() == null || pet.getOwner().getUser().getEmail() == null) return false;

        return ownerEmail.equalsIgnoreCase(pet.getOwner().getUser().getEmail());
    }

    @Override
    public boolean isAppointmentOwnedBy(String ownerEmail, String appointmentId) {
        if (ownerEmail == null || appointmentId == null || appointmentId.isBlank()) return false;

        Appointment a = appointmentRepository.findByAppointmentId(appointmentId).orElse(null);
        if (a == null) return false;

        if (a.getPet() == null) return false; // pet null ise owner'a ait saymayız
        if (a.getPet().getOwner() == null || a.getPet().getOwner().getUser().getEmail() == null) return false;

        return ownerEmail.equalsIgnoreCase(a.getPet().getOwner().getUser().getEmail());
    }

    @Override
    public List<AppointmentResponseDTO> getMyAppointments(String ownerEmail) {
        // Owner'ın tüm pet'leri -> onların appointment'ları
        // Performans için repository'de join query tercih edilir (aşağıdaki çözüm temel ve güvenli)
        List<Pet> myPets = petRepository.findByOwner_User_Email(ownerEmail); // repo methodu eklemen gerekecek
        if (myPets.isEmpty()) return List.of();

        List<Appointment> all = new ArrayList<>();
        for (Pet p : myPets) {
            all.addAll(appointmentRepository.findByPet_PetId(p.getPetId()));
        }

        return all.stream().map(appointmentMapper::toResponse).toList();
    }

    // ---------------------------
    // CRUD
    // ---------------------------

    @Override
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(String appointmentId) {
        Appointment a = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment bulunamadı: " + appointmentId));
        return appointmentMapper.toResponse(a);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByPetId(String petId) {
        return appointmentRepository.findByPet_PetId(petId)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByVeterinaryId(String vetId) {
        return appointmentRepository.findByVeterinary_VetId(vetId)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByClinicId(String clinicId) {
        List<Veterinary> vetList = veterinaryRepository.findByClinic_ClinicId(clinicId);

        List<Appointment> allAppointments = new ArrayList<>();
        for (Veterinary vet : vetList) {
            allAppointments.addAll(
                    appointmentRepository.findByVeterinary_VetId(vet.getVetId())
            );
        }

        return allAppointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentCreateRequestDTO dto) {

        if (dto.getVetId() == null || dto.getVetId().isBlank()) {
            throw new IllegalArgumentException("Appointment için vetId zorunlu.");
        }
        if (dto.getDateTime() == null) {
            throw new IllegalArgumentException("Appointment için dateTime zorunlu.");
        }

        LocalDate date = dto.getDateTime().toLocalDate();
        LocalTime time = dto.getDateTime().toLocalTime();

        Veterinary vet = veterinaryRepository.findByVetId(dto.getVetId())
                .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + dto.getVetId()));

        // pet opsiyonel ama varsa gerçekten var mı kontrol et
        Pet pet = null;
        if (dto.getPetId() != null && !dto.getPetId().isBlank()) {
            pet = petRepository.findByPetId(dto.getPetId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + dto.getPetId()));
        }

        // çakışma kontrolü: aynı vet + date + time
        boolean exists = appointmentRepository.existsByVeterinary_VetIdAndDateAndTime(
                dto.getVetId(), date, time
        );
        if (exists) {
            throw new IllegalStateException("Bu tarih ve saatte veteriner için zaten randevu var.");
        }

        Appointment appointment = new Appointment();

        // Kritik: appointmentId client'tan gelmesin daha iyi.
        // Ama siz string kullanıyorsanız mecburen id üretimini service tarafına taşıyın.
        if (dto.getAppointmentId() != null && !dto.getAppointmentId().isBlank()) {
            appointment.setAppointmentId(dto.getAppointmentId());
        }

        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setVeterinary(vet);
        appointment.setPet(pet);

        if (dto.getStatus() != null) appointment.setStatus(dto.getStatus());
        if (dto.getReason() != null) appointment.setReason(dto.getReason());

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateAppointment(String appointmentId, AppointmentUpdateRequestDTO dto) {

        Appointment existing = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment bulunamadı: " + appointmentId));

        // dateTime güncelle
        if (dto.getDateTime() != null) {
            LocalDate newDate = dto.getDateTime().toLocalDate();
            LocalTime newTime = dto.getDateTime().toLocalTime();

            String vetIdForCheck = existing.getVeterinary().getVetId();
            if (dto.getVetId() != null && !dto.getVetId().isBlank()) {
                vetIdForCheck = dto.getVetId();
            }

            boolean exists = appointmentRepository.existsByVeterinary_VetIdAndDateAndTime(
                    vetIdForCheck, newDate, newTime
            );

            if (exists && !(newDate.equals(existing.getDate()) && newTime.equals(existing.getTime())
                    && vetIdForCheck.equals(existing.getVeterinary().getVetId()))) {
                throw new IllegalStateException("Bu tarih ve saatte veteriner için zaten randevu var.");
            }

            existing.setDate(newDate);
            existing.setTime(newTime);
        }

        // pet değişimi
        if (dto.getPetId() != null) {
            if (dto.getPetId().isBlank()) {
                existing.setPet(null);
            } else {
                Pet newPet = petRepository.findByPetId(dto.getPetId())
                        .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + dto.getPetId()));
                existing.setPet(newPet);
            }
        }

        // vet değişimi
        if (dto.getVetId() != null && !dto.getVetId().isBlank()) {
            Veterinary newVet = veterinaryRepository.findByVetId(dto.getVetId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + dto.getVetId()));
            existing.setVeterinary(newVet);
        }

        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getReason() != null) existing.setReason(dto.getReason());

        Appointment saved = appointmentRepository.save(existing);
        return appointmentMapper.toResponse(saved);
    }

    @Transactional
    public void cancelAppointment(String appointmentId) {
        // 1. Önce randevuyu veritabanından buluyoruz.
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı: " + appointmentId));

        // 2. Randevu içindeki Slot nesnesine erişiyoruz.
        // DİKKAT: Appointment modeline eklediğin 'private Slot slot' alanı sayesinde buraya ulaşıyoruz.
        Slot slot = slotRepository.findBySlotId(appointment.getSlotId()).orElse(null);

        if (slot != null) {
            // 3. Slotun durumunu tekrar rezerve edilebilir (0 / false) yapıyoruz.
            slot.setBooked(false);
            slotRepository.save(slot);
            System.out.println("Slot id: " + slot.getSlotId() + " başarıyla boşa çıkarıldı.");
        }

        // 4. Randevu kaydını siliyoruz.
        appointmentRepository.delete(appointment);
    }
}
