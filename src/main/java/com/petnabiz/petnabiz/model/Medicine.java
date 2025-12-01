package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @Column(name = "medicine_id", length = 20)
    private String medicineId;

    private String name;
    private String type;

    public Medicine() {}

    // GETTERS & SETTERS
}
