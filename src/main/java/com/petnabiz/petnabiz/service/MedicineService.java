package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.medicine.MedicineCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicine.MedicineUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicine.MedicineResponseDTO;

import java.util.List;

public interface MedicineService {

    List<MedicineResponseDTO> getAllMedicines();

    MedicineResponseDTO getMedicineById(String medicineId);

    List<MedicineResponseDTO> searchByName(String namePart);

    List<MedicineResponseDTO> getMedicinesByType(String type);

    MedicineResponseDTO createMedicine(MedicineCreateRequestDTO dto);

    MedicineResponseDTO updateMedicine(String medicineId, MedicineUpdateRequestDTO dto);

    void deleteMedicine(String medicineId);
}
