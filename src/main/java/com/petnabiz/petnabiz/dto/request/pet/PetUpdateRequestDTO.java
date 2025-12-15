package com.petnabiz.petnabiz.dto.request.pet;
import lombok.Data;

@Data
public class PetUpdateRequestDTO {
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
}

