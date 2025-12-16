package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;

import java.util.List;

public interface VeterinaryService {

    List<VeterinaryResponseDTO> getAllVeterinaries();

    VeterinaryResponseDTO getVeterinaryById(String vetId);

    List<VeterinaryResponseDTO> getVeterinariesByClinicId(String clinicId);

    List<VeterinaryResponseDTO> searchByFirstName(String firstNamePart);

    List<VeterinaryResponseDTO> searchByLastName(String lastNamePart);

    VeterinaryResponseDTO getByPhoneNumber(String phoneNumber);

    List<VeterinaryResponseDTO> searchByCertificate(String certificatePart);

    VeterinaryResponseDTO createVeterinary(VeterinaryCreateRequestDTO dto);

    VeterinaryResponseDTO updateVeterinary(String vetId, VeterinaryUpdateRequestDTO dto);

    void deleteVeterinary(String vetId);
}
