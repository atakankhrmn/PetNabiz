package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "admin_id")
    private String adminId; // = userId

    @OneToOne
    @MapsId
    @JoinColumn(name = "admin_id")
    private User user;

    private String fullName;

    public Admin() {}

    // GETTERS & SETTERS
}
