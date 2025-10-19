package com.encentral.repositories;


import com.encentral.entities.Attendance;
import com.encentral.entities.User;

import javax.inject.Inject;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

public class AttendanceRepository {
    private final EntityManager em;

    @Inject
    public AttendanceRepository(EntityManager em) {
        this.em = em;
    }

    public Attendance save(Attendance a) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (a.id == null) em.persist(a);
            else em.merge(a);
            tx.commit();
            return a;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    public List<Attendance> findByUserAndDate(User user, LocalDate date) {
        TypedQuery<Attendance> q = em.createQuery("SELECT a FROM Attendance a WHERE a.user = :user AND a.dateRecorded = :date", Attendance.class);
        q.setParameter("user", user);
        q.setParameter("date", date);
        return q.getResultList();
    }
}

