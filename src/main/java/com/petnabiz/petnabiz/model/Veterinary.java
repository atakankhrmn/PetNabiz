package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "veterinary")
public class Veterinary {

    @Id
    @Column(name = "vet_id", length = 20)
    private String vetId;   // Kendi ID'si, User ile ortak değil

    @Column(nullable = false, length = 64)
    private String firstName;

    @Column(nullable = false, length = 64)
    private String lastName;

    @Column(length = 32)
    private String phoneNumber;

    @Column(length = 64)
    private String city;

    @Column(length = 64)
    private String district;

    @Column(length = 256)
    private String address;

    @Column(length = 128)
    private String certificate;   // diploma / sertifika bilgisi

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @OneToMany(mappedBy = "veterinary")
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "veterinary")
    private List<MedicalRecord> medicalRecords;


    public Veterinary() {
    }

    public Veterinary(String vetId, String firstName, String lastName,
                      String phoneNumber,
                      String city, String district, String address,
                      String certificate, Clinic clinic) {
        this.vetId = vetId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.district = district;
        this.address = address;
        this.certificate = certificate;
        this.clinic = clinic;
    }

    // GETTERS & SETTERS

}
