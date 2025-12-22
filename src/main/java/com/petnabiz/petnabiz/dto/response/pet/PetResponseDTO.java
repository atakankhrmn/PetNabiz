package com.petnabiz.petnabiz.dto.response.pet;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PetResponseDTO {
    private String petId;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private String photoUrl;
    private LocalDate birthDate;
    private double weight;

    private String ownerId;

    // ilişkileri full basmayalım, ID listesi yeter
    private List<String> appointmentIds;
    private List<String> medicalRecordIds;
    private String ownerName;
}
