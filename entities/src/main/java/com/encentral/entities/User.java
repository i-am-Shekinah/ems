package com.encentral.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User {
    public enum Role { ADMIN, EMPLOYEE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public UUID id;

    @Column(nullable = false)
    public String firstname;

    @Column(nullable = false)
    public String lastname;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String passwordHash; // store hashed password!

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;

    @Column(nullable = false, unique = true)
    public String token; // UUID string

    @Column
    public String pin; // 4-digit pin for employee sign in (null for admin)

    @Column
    public LocalDateTime createdAt;

    public User() {}

    public User(String firstname, String lastname, String email, String passwordHash, Role role, String pin) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.pin = pin;
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
