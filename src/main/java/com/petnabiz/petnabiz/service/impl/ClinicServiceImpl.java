package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.clinic.ClinicCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.clinic.ClinicUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinic.ClinicResponseDTO;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;
import com.petnabiz.petnabiz.mapper.ClinicMapper;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.ClinicApplication;
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
import java.util.UUID;

@Service("clinicService") // @PreAuthorize içindeki @clinicService için net bean adı
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

    // ---------------------------
    // Security helper (SpEL için)
    // ---------------------------
    @Override
    public boolean isClinicSelf(String clinicEmail, String clinicId) {
        if (clinicEmail == null || clinicId == null || clinicId.isBlank()) return false;

        Clinic clinic = clinicRepository.findByClinicId(clinicId).orElse(null);
        if (clinic == null) return false;

        if (clinic.getUser() == null || clinic.getUser().getEmail() == null) return false;

        return clinicEmail.equalsIgnoreCase(clinic.getUser().getEmail());
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
        Clinic clinic = clinicRepository.findByUser_Email(email)
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

        // ID çakışma kontrolü
        if (clinicRepository.existsByClinicId(dto.getClinicId()) || userRepository.existsById(dto.getClinicId())) {
            throw new IllegalArgumentException("Bu clinicId zaten kullanılıyor: " + dto.getClinicId());
        }

        // email unique kontrolü
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
        }

        User user = new User();
        user.setUserId(dto.getClinicId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO: BCrypt
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

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCity() != null) existing.setCity(dto.getCity());
        if (dto.getDistrict() != null) existing.setDistrict(dto.getDistrict());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());

        if (existing.getUser() == null) {
            throw new IllegalStateException("Clinic'in user kaydı yok: " + clinicId);
        }

        if (dto.getEmail() != null) {
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
    @Transactional
    public void deleteClinic(String clinicId) {
        // deleteById yerine entity üzerinden sil: PK mismatch riskini bitirir
        Clinic existing = clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen klinik bulunamadı: " + clinicId));

        clinicRepository.delete(existing);

        // İstersen user'ı da cascade/manuel sil (iş kuralı):
        // userRepository.delete(existing.getUser());
    }

    @Override
    public List<VetSummaryDTO> getVeterinariesByClinic(String clinicId) {
        clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + clinicId));

        List<Veterinary> vets = veterinaryRepository.findByClinic_ClinicId(clinicId);
        return vets.stream().map(clinicMapper::toVetSummary).toList();
    }

    /**
     * ADMIN APPROVE → ClinicApplication → User + Clinic oluşturur
     */
    @Override
    @Transactional
    public void createClinicFromApplication(ClinicApplication app) {

        if (userRepository.existsByEmail(app.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        String userId = generateClinicUserId();

        User user = new User();
        user.setUserId(userId);
        user.setEmail(app.getEmail());
        user.setPassword(app.getPassword()); // hashli
        user.setActive(true);
        user.setRole("ROLE_CLINIC"); // ✅ hasRole ile uyumlu

        // ✅ kritik: managed user'ı al
        User savedUser = userRepository.save(user);

        Clinic clinic = new Clinic();
        clinic.setUser(savedUser); // ✅ savedUser ver
        clinic.setName(app.getClinicName());
        clinic.setCity(app.getCity());
        clinic.setDistrict(app.getDistrict());
        clinic.setAddress(app.getAddress());
        clinic.setPhone(app.getPhone());

        clinicRepository.save(clinic);
    }


    /**
     * C-XXXXXXX formatında userId üretir
     */
    private String generateClinicUserId() {
        return "C-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
