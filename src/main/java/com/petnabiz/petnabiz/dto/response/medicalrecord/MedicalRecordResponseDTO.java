package com.petnabiz.petnabiz.dto.response.medicalrecord;

import lombok.Data;
import java.time.LocalDateTime;
import com.petnabiz.petnabiz.dto.summary.*;

@Data
public class MedicalRecordResponseDTO {
    private String recordId;
    private LocalDateTime createdAt;
    private String diagnosis;
    private String notes;
    private String prescriptions;
    private PetSummaryDTO pet;
    private VetSummaryDTO vet;
    private OwnerSummaryDTO owner;
}
