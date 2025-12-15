package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.appointment.AppointmentCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.appointment.AppointmentUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.appointment.AppointmentResponseDTO;
import com.petnabiz.petnabiz.mapper.AppointmentMapper;
import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.AppointmentRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PetRepository petRepository,
                                  VeterinaryRepository veterinaryRepository,
                                  AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.appointmentMapper = appointmentMapper;
    }

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

        // pet opsiyonel
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

        // appointmentId client’tan geliyorsa set et, yoksa DB/servis üretmeli (sizde string, o yüzden opsiyon bıraktım)
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

            // eğer vet de değişiyorsa önce onu belirle, çakışmayı ona göre kontrol et
            String vetIdForCheck = existing.getVeterinary().getVetId();
            if (dto.getVetId() != null && !dto.getVetId().isBlank()) {
                vetIdForCheck = dto.getVetId();
            }

            boolean exists = appointmentRepository.existsByVeterinary_VetIdAndDateAndTime(
                    vetIdForCheck, newDate, newTime
            );

            // aynı randevunun kendisiyle çakışma riskini azaltmak için (repo’da id dışlama yoksa) basit check:
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
                // boş string gelirse pet'i null'la (pet nullable)
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

    @Override
    public void deleteAppointment(String appointmentId) {
        boolean exists = appointmentRepository.existsById(appointmentId);
        if (!exists) {
            throw new IllegalArgumentException("Silinecek appointment bulunamadı: " + appointmentId);
        }
        appointmentRepository.deleteById(appointmentId);
    }
}
