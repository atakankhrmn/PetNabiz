package com.petnabiz.petnabiz.dto.request.pet;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PetCreateRequestDTO {
    private String petId;      // opsiyonel (siz veriyorsanız)
    private String name;
    private String species;    // zorunlu
    private String breed;
    private String gender;     // zorunlu
    private String photoUrl;
    private LocalDate birthDate;
    private Double weight;

    private String ownerId;    // zorunlu
}
