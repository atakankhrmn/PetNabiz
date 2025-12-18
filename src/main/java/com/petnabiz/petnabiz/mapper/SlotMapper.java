package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.slot.SlotResponseDTO;
import com.petnabiz.petnabiz.model.Slot;
import org.springframework.stereotype.Component;

@Component
public class SlotMapper {

    public SlotResponseDTO toResponse(Slot s) {
        SlotResponseDTO dto = new SlotResponseDTO();
        dto.setSlotId(s.getSlotId());
        dto.setDate(s.getDate());
        dto.setTime(s.getTime());
        dto.setBooked(s.isBooked());
        dto.setVetId(s.getVeterinary() != null ? s.getVeterinary().getVetId() : null);

        String fullName = s.getVeterinary().getFirstName() + " " + s.getVeterinary().getLastName();
        dto.setVetName(s.getVeterinary() != null ? fullName : null);

        String clinicName = s.getVeterinary().getClinic().getName();
        dto.setClinicName(s.getVeterinary().getClinic() != null ? clinicName : null);

        String clinicAddress = s.getVeterinary().getClinic().getAddress() +  " " + s.getVeterinary().getClinic().getCity() + "/" + s.getVeterinary().getClinic().getDistrict();
        dto.setClinicAddress(s.getVeterinary().getClinic() != null ? clinicAddress : null);

        return dto;
    }
}
