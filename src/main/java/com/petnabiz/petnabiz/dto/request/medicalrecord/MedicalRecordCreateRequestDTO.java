package com.petnabiz.petnabiz.dto.request.medicalrecord;

import lombok.Data;

@Data
public class MedicalRecordCreateRequestDTO {
    private String appointmentId;
    private String diagnosis;
    private String notes;
    private String prescriptions;
}
