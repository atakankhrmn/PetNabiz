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

    @Column(nullable = false)
    private String species;

    private String breed;

    @Column(nullable = false)
    private String gender;

    private String photoUrl;
    private LocalDate birthDate;
    private double weight;


    @ManyToOne
    @JoinColumn(name = "owner_id",nullable = false)
    private PetOwner owner;

    @OneToMany(mappedBy = "pet")
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "pet")
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "pet")
    private List<Medication> medications;

    public Pet() {}

    // GETTERS & SETTERS


    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public PetOwner getOwner() {
        return owner;
    }

    public void setOwner(PetOwner owner) {
        this.owner = owner;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }
}
