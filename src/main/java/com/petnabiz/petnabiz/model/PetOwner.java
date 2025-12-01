package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pet_owner")
public class PetOwner {

    @Id
    @Column(name = "owner_id")
    private String ownerId; // = userId

    @OneToOne
    @MapsId
    @JoinColumn(name = "owner_id")
    private User user;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    @OneToMany(mappedBy = "owner")
    private List<Pet> pets;

    public PetOwner() {}

    public PetOwner(User user, String firstName, String lastName, String phone, String address) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }

    // GETTERS & SETTERS
}
