package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @Column(name = "pet_id", length = 20)
    private String petId;

    private String name;
    private String species;
    private String breed;
    private String gender;
    private String photoUrl;
    private LocalDate birthDate;
    private double weight;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PetOwner owner;

    @OneToMany(mappedBy = "pet")
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "pet")
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "pet")
    private List<Medication> medications;

    public Pet() {}

    // GETTERS & SETTERS
}
