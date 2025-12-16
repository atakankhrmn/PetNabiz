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
        return dto;
    }
}
