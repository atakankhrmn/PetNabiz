package com.petnabiz.petnabiz.dto.request.pet;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PetUpdateRequestDTO {
    private String name;
    private String species;
    private String breed;
    private String gender;
    private String photoUrl;
    private LocalDate birthDate;
    private Double weight;

    private String ownerId; // owner değiştirmek istersen
}
