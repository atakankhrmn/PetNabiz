package com.petnabiz.petnabiz.dto.response.slot;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SlotGenerateResponseDTO {
    private String message;
    private String vetId;
    private LocalDate date;
}
