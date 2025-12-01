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

    @Column(nullable = false)
    private String fullName;

    public Admin() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // GETTERS & SETTERS
}
