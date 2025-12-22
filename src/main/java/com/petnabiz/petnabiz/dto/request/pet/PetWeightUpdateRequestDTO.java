package com.petnabiz.petnabiz.dto.request.pet;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PetWeightUpdateRequestDTO {
    private String petId;
    private Double weight;
}
