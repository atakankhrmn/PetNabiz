package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {

    @Id
    @Column(name = "record_id", length = 20)
    private String recordId;

    private String description;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "vet_id")
    private Veterinary veterinary;

    @OneToMany(mappedBy = "medicalRecord")
    private List<Medication> medications;

    public MedicalRecord() {}

    // GETTERS & SETTERS
}
