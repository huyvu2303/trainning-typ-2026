package com.courseshop.courseshop.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // Đặt tên bảng là users trong MySQL
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private LocalDate dob; // Ngày sinh (YYYY-MM-DD)
    private boolean isCompleted;

    // 1. Constructor không tham số (Bắt buộc cho JPA)
    public User() {}

    // 2. Constructor đầy đủ tham số
    public User(String name, String email, LocalDate dob, boolean isCompleted) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", dob=" + dob + ", isCompleted=" + isCompleted + "]";
    }
}
