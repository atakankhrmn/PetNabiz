package com.petnabiz.petnabiz.dto.request.medicine;

import lombok.Data;

@Data
public class MedicineCreateRequestDTO {
    private String medicineId; // opsiyonel (siz veriyorsanız)
    private String name;       // zorunlu
    private String type;       // zorunlu
}
