package com.petnabiz.petnabiz.dto.response.owner;

import lombok.Data;
import java.util.List;
import com.petnabiz.petnabiz.dto.summary.PetSummaryDTO;

@Data
public class PetOwnerResponseDTO {
    private String ownerId;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private boolean active;
    private List<PetSummaryDTO> pets;
}
