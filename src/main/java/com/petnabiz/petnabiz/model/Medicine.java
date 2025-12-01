package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @Column(name = "medicine_id", length = 20)
    private String medicineId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    public Medicine() {}

    // GETTERS & SETTERS


    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
