package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.medicine.MedicineResponseDTO;
import com.petnabiz.petnabiz.model.Medicine;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper {

    public MedicineResponseDTO toResponse(Medicine m) {
        MedicineResponseDTO dto = new MedicineResponseDTO();
        dto.setMedicineId(m.getMedicineId());
        dto.setName(m.getName());
        dto.setType(m.getType());
        return dto;
    }
}
