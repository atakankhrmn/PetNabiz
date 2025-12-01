package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @Column(name = "appointment_id", length = 20)
    private String appointmentId;

    private LocalDate date;
    private LocalTime time;
    private String status;
    private String reason;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "vet_id")
    private Veterinary veterinary;


    public Appointment() {}

    // GETTERS & SETTERS
}
