package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(length = 256, nullable = false, unique = true)
    private String email;

    @Column(length = 256, nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isActive;

    @Column(length = 32, nullable = false)
    private String role;

    public User() {}

    public User(String userId, String email, String password, boolean isActive, String role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.role = role;
    }

    // GETTERS & SETTERS


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
