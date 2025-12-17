package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.clinicapplication.ClinicApplicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinicapplication.ClinicApplicationResponseDTO;
import com.petnabiz.petnabiz.model.ApplicationStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClinicApplicationService {

    Long createApplication(ClinicApplicationCreateRequestDTO dto, MultipartFile document);

    List<ClinicApplicationResponseDTO> listByStatus(ApplicationStatus status);

    void approve(Long applicationId, String adminId);

    void reject(Long applicationId, String adminId);
}
