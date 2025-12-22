package com.petnabiz.petnabiz.dto.response.clinic;

import lombok.Data;
import java.util.List;
import com.petnabiz.petnabiz.dto.summary.VetSummaryDTO;

@Data
public class ClinicResponseDTO {
    private String clinicId;
    private String email;      // user.email
    private boolean active;    // user.active

    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;

    private List<VetSummaryDTO> veterinaries;
}
