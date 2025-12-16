package com.petnabiz.petnabiz.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "clinic_application",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_clinic_app_email", columnNames = "email")
        }
)
public class ClinicApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Column(name = "clinic_name", nullable = false, length = 150)
    private String clinicName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(nullable = false, length = 60)
    private String district;

    @Column(nullable = false, length = 255)
    private String address;

    // Register sırasında gelen password'ün HASH'lenmiş hali tutulur
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // Yüklenen belgeyi dosya sisteminde saklayıp path tutuyoruz
    @Column(name = "document_path", nullable = false, length = 500)
    private String documentPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // Sizde Admin/User id tipi String olduğu için String tuttum
    @Column(name = "reviewed_by_admin_id", length = 20)
    private String reviewedByAdminId;

    public ClinicApplication() {}

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getReviewedByAdminId() {
        return reviewedByAdminId;
    }

    public void setReviewedByAdminId(String reviewedByAdminId) {
        this.reviewedByAdminId = reviewedByAdminId;
    }


}
