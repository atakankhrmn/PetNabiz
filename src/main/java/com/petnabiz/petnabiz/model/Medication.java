package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "medication")
public class Medication {

    @Id
    @Column(name = "medication_id", length = 20)
    private String medicationId;


    private String instructions;
    private LocalDate start;
    private LocalDate end;

//    @ManyToOne
//    @JoinColumn(name = "record_id")
//    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    public Medication() {}

    // GETTERS & SETTERS
}
