package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import com.petnabiz.petnabiz.mapper.VeterinaryMapper;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.ClinicRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.VeterinaryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("veterinaryService") // SpEL için
public class VeterinaryServiceImpl implements VeterinaryService {

    private final VeterinaryRepository veterinaryRepository;
    private final ClinicRepository clinicRepository;
    private final VeterinaryMapper veterinaryMapper;

    public VeterinaryServiceImpl(VeterinaryRepository veterinaryRepository,
                                 ClinicRepository clinicRepository,
                                 VeterinaryMapper veterinaryMapper) {
        this.veterinaryRepository = veterinaryRepository;
        this.clinicRepository = clinicRepository;
        this.veterinaryMapper = veterinaryMapper;
    }

    // ---- helpers ----
    @Override
    public boolean isClinicOwner(String clinicEmail, String clinicId) {
        if (clinicEmail == null || clinicEmail.isBlank() || clinicId == null || clinicId.isBlank()) return false;

        Clinic clinic = clinicRepository.findByClinicId(clinicId).orElse(null);
        if (clinic == null || clinic.getUser() == null || clinic.getUser().getEmail() == null) return false;

        return clinicEmail.equalsIgnoreCase(clinic.getUser().getEmail());
    }

    @Override
    public boolean isClinicOwnerOfVet(String clinicEmail, String vetId) {
        if (clinicEmail == null || clinicEmail.isBlank() || vetId == null || vetId.isBlank()) return false;

        Veterinary vet = veterinaryRepository.findByVetId(vetId).orElse(null);
        if (vet == null || vet.getClinic() == null || vet.getClinic().getUser() == null) return false;

        String email = vet.getClinic().getUser().getEmail();
        return email != null && clinicEmail.equalsIgnoreCase(email);
    }

    // ---- reads ----
    @Override
    public List<VeterinaryResponseDTO> getAllVeterinaries() {
        return veterinaryRepository.findAll().stream()
                .map(veterinaryMapper::toResponse)
                .toList();
    }

    @Override
    public VeterinaryResponseDTO getVeterinaryById(String vetId) {
        Veterinary v = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Veterinary bulunamadı: " + vetId));
        return veterinaryMapper.toResponse(v);
    }

    @Override
    public List<VeterinaryResponseDTO> getVeterinariesByClinicId(String clinicId) {
        clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + clinicId));

        return veterinaryRepository.findByClinic_ClinicId(clinicId).stream()
                .map(veterinaryMapper::toResponse)
                .toList();
    }

    // ---- writes ----
    @Override
    @Transactional
    public VeterinaryResponseDTO createVeterinary(VeterinaryCreateRequestDTO dto) {

        if (dto.getVetId() == null || dto.getVetId().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary ID boş olamaz.");
        }
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary firstName boş olamaz.");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary lastName boş olamaz.");
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary phoneNumber boş olamaz.");
        }

        if (veterinaryRepository.existsByVetId(dto.getVetId())) {
            throw new IllegalStateException("Bu vetId ile zaten bir veterinary kayıtlı: " + dto.getVetId());
        }

        Veterinary v = new Veterinary();
        v.setVetId(dto.getVetId());
        v.setFirstName(dto.getFirstName());
        v.setLastName(dto.getLastName());
        v.setPhoneNumber(dto.getPhoneNumber());
        v.setAddress(dto.getAddress());
        v.setCertificate(dto.getCertificate());

        // clinic zorunlu olsun istiyorsan burada check koy:
        if (dto.getClinicId() != null && !dto.getClinicId().trim().isEmpty()) {
            Clinic clinic = clinicRepository.findByClinicId(dto.getClinicId())
                    .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + dto.getClinicId()));
            v.setClinic(clinic);
        }

        Veterinary saved = veterinaryRepository.save(v);
        return veterinaryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public VeterinaryResponseDTO updateVeterinary(String vetId, VeterinaryUpdateRequestDTO dto) {

        Veterinary existing = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Veterinary bulunamadı: " + vetId));

        if (dto.getFirstName() != null && !dto.getFirstName().trim().isEmpty()) {
            existing.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().trim().isEmpty()) {
            existing.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            existing.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddress() != null) {
            existing.setAddress(dto.getAddress());
        }
        if (dto.getCertificate() != null) {
            existing.setCertificate(dto.getCertificate());
        }

        // Klinik değiştirme CLINIC için tehlikeli -> bence sadece ADMIN yapmalı.
        // Yine de bırakacaksan:
        if (dto.getClinicId() != null && !dto.getClinicId().trim().isEmpty()) {
            Clinic newClinic = clinicRepository.findByClinicId(dto.getClinicId())
                    .orElseThrow(() -> new EntityNotFoundException("Yeni clinic bulunamadı: " + dto.getClinicId()));
            existing.setClinic(newClinic);
        }

        Veterinary saved = veterinaryRepository.save(existing);
        return veterinaryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteVeterinary(String vetId) {
        Veterinary v = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen veterinary bulunamadı: " + vetId));
        veterinaryRepository.delete(v);
    }
}
