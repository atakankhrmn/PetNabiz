package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.clinic.ClinicCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.clinic.ClinicUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.clinic.ClinicResponseDTO;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;

import java.util.List;

public interface ClinicService {

    List<ClinicResponseDTO> getAllClinics();

    ClinicResponseDTO getClinicById(String clinicId);

    List<ClinicResponseDTO> searchClinicsByName(String namePart);

    ClinicResponseDTO getClinicByEmail(String email);

    ClinicResponseDTO createClinic(ClinicCreateRequestDTO dto);

    ClinicResponseDTO updateClinic(String clinicId, ClinicUpdateRequestDTO dto);

    void deleteClinic(String clinicId);

    List<VetSummaryDTO> getVeterinariesByClinic(String clinicId);

    boolean isClinicSelf(String clinicEmail, String clinicId);

}
