package com.petnabiz.petnabiz.dto.response.pet;

import lombok.Data;
import com.petnabiz.petnabiz.dto.summary.OwnerSummaryDTO;

@Data
public class PetResponseDTO {
    private String petId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private OwnerSummaryDTO owner;
}
