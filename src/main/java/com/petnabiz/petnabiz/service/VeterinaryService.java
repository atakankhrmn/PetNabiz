package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;

import java.util.List;

public interface VeterinaryService {

    List<VeterinaryResponseDTO> getAllVeterinaries();

    VeterinaryResponseDTO getVeterinaryById(String vetId);

    List<VeterinaryResponseDTO> getVeterinariesByClinicId(String clinicId);

    VeterinaryResponseDTO createVeterinary(VeterinaryCreateRequestDTO dto);

    VeterinaryResponseDTO updateVeterinary(String vetId, VeterinaryUpdateRequestDTO dto);

    void deleteVeterinary(String vetId);

    // SpEL / security helpers
    boolean isClinicOwner(String clinicEmail, String clinicId);

    boolean isClinicOwnerOfVet(String clinicEmail, String vetId);
}
