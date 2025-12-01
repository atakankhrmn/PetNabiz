package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "clinic")
public class Clinic {

    @Id
    @Column(name = "clinic_id", length = 20)
    private String clinicId;   // = userId

    @OneToOne
    @MapsId
    @JoinColumn(name = "clinic_id")
    private User user;

    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;

    @OneToMany(mappedBy = "clinic")
    private List<Veterinary> veterinaries;

//    @OneToMany(mappedBy = "clinic")
//    private List<Appointment> appointments;

    public Clinic() {}

    public Clinic(User user, String name, String city, String district, String address, String phone) {
        this.user = user;
        this.name = name;
        this.city = city;
        this.district = district;
        this.address = address;
        this.phone = phone;
    }

    // GETTERS & SETTERS

}
