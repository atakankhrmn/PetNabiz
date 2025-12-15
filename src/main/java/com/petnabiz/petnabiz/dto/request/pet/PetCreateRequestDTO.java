package com.petnabiz.petnabiz.dto.request.pet;

import lombok.Data;

@Data
public class PetCreateRequestDTO {
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private String ownerId;
}
