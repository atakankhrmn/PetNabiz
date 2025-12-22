package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.pet.PetResponseDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VeterinaryService {

    List<VeterinaryResponseDTO> getAllVeterinaries();

    VeterinaryResponseDTO getVeterinaryById(String vetId);

    List<VeterinaryResponseDTO> getVeterinariesByClinicId(String clinicId);

    VeterinaryResponseDTO createVeterinary(VeterinaryCreateRequestDTO dto ,  MultipartFile file);

    VeterinaryResponseDTO updateVeterinary(String vetId, VeterinaryUpdateRequestDTO dto);

    void deleteVeterinary(String vetId);

    // SpEL / security helpers
    boolean isClinicOwner(String clinicEmail, String clinicId);

    boolean isClinicOwnerOfVet(String clinicEmail, String vetId);

    public List<VeterinaryResponseDTO> getAllMyVeterinaries();

    public Resource getCertificateResource(String vetId);
}
