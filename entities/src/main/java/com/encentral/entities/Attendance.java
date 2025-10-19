package com.encentral.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances", indexes = {@Index(name = "idx_att_user_date", columnList = "user_id, dateRecorded")})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @Column(nullable = false)
    public LocalDate dateRecorded; // day of attendance (yyyy-MM-dd)

    @Column(nullable = false)
    public LocalDateTime recordedAt; // exact timestamp when marked

    public Attendance() {}

    public Attendance(User user, LocalDate dateRecorded, LocalDateTime recordedAt) {
        this.user = user;
        this.dateRecorded = dateRecorded;
        this.recordedAt = recordedAt;
    }
}
