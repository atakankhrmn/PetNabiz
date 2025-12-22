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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veterinary> veterinaries;

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


    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Veterinary> getVeterinaries() {
        return veterinaries;
    }

    public void setVeterinaries(List<Veterinary> veterinaries) {
        this.veterinaries = veterinaries;
    }

    public String getEmail() {
        return  user.getEmail();
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }
}
