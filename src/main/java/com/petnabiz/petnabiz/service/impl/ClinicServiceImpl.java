package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.clinic.ClinicCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.clinic.ClinicUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinic.ClinicResponseDTO;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;
import com.petnabiz.petnabiz.mapper.ClinicMapper;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.ClinicRepository;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.ClinicService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final UserRepository userRepository;
    private final ClinicMapper clinicMapper;

    public ClinicServiceImpl(ClinicRepository clinicRepository,
                             VeterinaryRepository veterinaryRepository,
                             UserRepository userRepository,
                             ClinicMapper clinicMapper) {
        this.clinicRepository = clinicRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.userRepository = userRepository;
        this.clinicMapper = clinicMapper;
    }

    @Override
    public List<ClinicResponseDTO> getAllClinics() {
        return clinicRepository.findAll()
                .stream()
                .map(clinicMapper::toResponse)
                .toList();
    }

    @Override
    public ClinicResponseDTO getClinicById(String clinicId) {
        Clinic clinic = clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + clinicId));
        return clinicMapper.toResponse(clinic);
    }

    @Override
    public List<ClinicResponseDTO> searchClinicsByName(String namePart) {
        return clinicRepository.findByNameContainingIgnoreCase(namePart)
                .stream()
                .map(clinicMapper::toResponse)
                .toList();
    }

    @Override
    public ClinicResponseDTO getClinicByEmail(String email) {
        // Senin repo’da findByEmail varsa kullanabilirsin; ama email User’da olduğu için en garantisi user üzerinden gitmek:
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı (email): " + email));

        Clinic clinic = clinicRepository.findByClinicId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı (email): " + email));

        return clinicMapper.toResponse(clinic);
    }

    @Override
    @Transactional
    public ClinicResponseDTO createClinic(ClinicCreateRequestDTO dto) {

        if (dto.getClinicId() == null || dto.getClinicId().isBlank()) {
            throw new IllegalArgumentException("clinicId zorunlu.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("email zorunlu.");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("password zorunlu.");
        }

        // email unique kontrolü user üzerinden
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
        }

        // user oluştur (MapsId: clinicId = userId)
        User user = new User();
        user.setUserId(dto.getClinicId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO: hashle
        user.setRole("ROLE_CLINIC");
        user.setActive(true);

        userRepository.save(user);

        Clinic clinic = new Clinic();
        clinic.setClinicId(user.getUserId());
        clinic.setUser(user);

        clinic.setName(dto.getName());
        clinic.setCity(dto.getCity());
        clinic.setDistrict(dto.getDistrict());
        clinic.setAddress(dto.getAddress());
        clinic.setPhone(dto.getPhone());

        Clinic saved = clinicRepository.save(clinic);
        return clinicMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClinicResponseDTO updateClinic(String clinicId, ClinicUpdateRequestDTO dto) {

        Clinic existing = clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + clinicId));

        // clinic alanları (null değilse güncelle)
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCity() != null) existing.setCity(dto.getCity());
        if (dto.getDistrict() != null) existing.setDistrict(dto.getDistrict());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());

        // user alanları
        if (existing.getUser() == null) {
            throw new IllegalStateException("Clinic'in user kaydı yok: " + clinicId);
        }

        if (dto.getEmail() != null) {
            // email çakışma kontrolü
            userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getUserId().equals(existing.getUser().getUserId())) {
                    throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
                }
            });
            existing.getUser().setEmail(dto.getEmail());
        }

        if (dto.getActive() != null) {
            existing.getUser().setActive(dto.getActive());
        }

        Clinic saved = clinicRepository.save(existing);
        return clinicMapper.toResponse(saved);
    }

    @Override
    public void deleteClinic(String clinicId) {
        boolean exists = clinicRepository.existsByClinicId(clinicId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen klinik bulunamadı: " + clinicId);
        }
        clinicRepository.deleteById(clinicId);
    }

    @Override
    public List<VetSummaryDTO> getVeterinariesByClinic(String clinicId) {
        clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + clinicId));

        List<Veterinary> vets = veterinaryRepository.findByClinic_ClinicId(clinicId);
        return vets.stream().map(clinicMapper::toVetSummary).toList();
    }
}
