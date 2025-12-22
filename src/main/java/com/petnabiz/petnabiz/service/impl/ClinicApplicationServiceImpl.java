package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.clinicapplication.ClinicApplicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinicapplication.ClinicApplicationResponseDTO;
import com.petnabiz.petnabiz.model.ApplicationStatus;
import com.petnabiz.petnabiz.model.ClinicApplication;
import com.petnabiz.petnabiz.repository.ClinicApplicationRepository;
import com.petnabiz.petnabiz.service.ClinicApplicationService;
import com.petnabiz.petnabiz.service.ClinicService;
import com.petnabiz.petnabiz.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ClinicApplicationServiceImpl implements ClinicApplicationService {

    private final ClinicApplicationRepository clinicApplicationRepository;
    private final FileStorageService fileStorageService;

    // ✅ En temiz çözüm: approve sırasında clinic hesabını oluşturan servis
    private final ClinicService clinicService;

    public ClinicApplicationServiceImpl(
            ClinicApplicationRepository clinicApplicationRepository,
            FileStorageService fileStorageService,
            ClinicService clinicService
    ) {
        this.clinicApplicationRepository = clinicApplicationRepository;
        this.fileStorageService = fileStorageService;
        this.clinicService = clinicService;
    }

    @Override
    @Transactional
    public Long createApplication(ClinicApplicationCreateRequestDTO dto, MultipartFile document) {

        if (clinicApplicationRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An application with this email already exists");
        }

        String storedPath = fileStorageService.storeClinicDocument(document);

        ClinicApplication app = new ClinicApplication();
        app.setClinicName(dto.getClinicName());
        app.setEmail(dto.getEmail());
        app.setPhone(dto.getPhone());
        app.setCity(dto.getCity());
        app.setDistrict(dto.getDistrict());
        app.setAddress(dto.getAddress());

        app.setPassword(dto.getPassword());

        app.setDocumentPath(storedPath);
        app.setStatus(ApplicationStatus.PENDING);

        ClinicApplication saved = clinicApplicationRepository.save(app);
        return saved.getId();
    }

    @Override
    public List<ClinicApplicationResponseDTO> listByStatus(ApplicationStatus status) {
        return clinicApplicationRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void approve(Long applicationId, String adminId) {

        ClinicApplication app = clinicApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING applications can be approved");
        }

        // ✅ Burada gerçek Clinic(User) oluşturma:
        // ÖNERİ: ClinicService içine bu methodu ekleyin.
        // Method imzası ör: createClinicFromApplication(ClinicApplication app)
        clinicService.createClinicFromApplication(app);

        app.setStatus(ApplicationStatus.APPROVED);
        app.setReviewedByAdminId(adminId);

        clinicApplicationRepository.save(app);
    }

    @Override
    @Transactional
    public void reject(Long applicationId, String adminId) {

        ClinicApplication app = clinicApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING applications can be rejected");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setReviewedByAdminId(adminId);

        clinicApplicationRepository.save(app);
    }

    private ClinicApplicationResponseDTO toResponse(ClinicApplication app) {
        ClinicApplicationResponseDTO dto = new ClinicApplicationResponseDTO();
        dto.setId(app.getId());
        dto.setClinicName(app.getClinicName());
        dto.setEmail(app.getEmail());
        dto.setPhone(app.getPhone());
        dto.setCity(app.getCity());
        dto.setDistrict(app.getDistrict());
        dto.setAddress(app.getAddress());
        dto.setStatus(app.getStatus());
        dto.setDocumentPath(app.getDocumentPath());
        return dto;
    }
}

