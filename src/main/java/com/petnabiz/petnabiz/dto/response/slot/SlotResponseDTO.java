package com.petnabiz.petnabiz.dto.response.slot;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotResponseDTO {
    private Long slotId;
    private String vetId;
    private LocalDate date;
    private LocalTime time;
    private boolean booked;
}
