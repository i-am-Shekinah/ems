package com.encentral.repositories;


import com.encentral.entities.User;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final EntityManager em;

    @Inject
    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public User save(User u) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (u.id == null) em.persist(u);
            else em.merge(u);
            tx.commit();
            return u;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    public Optional<User> findByEmail(String email) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        q.setParameter("email", email);
        try {
            return Optional.of(q.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<User> findByToken(String token) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.token = :token", User.class);
        q.setParameter("token", token);
        try { return Optional.of(q.getSingleResult()); }
        catch (NoResultException ex) { return Optional.empty(); }
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public List<User> getAllEmployees() {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class);
        q.setParameter("role", User.Role.EMPLOYEE);
        return q.getResultList();
    }

    public void delete(User u) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User managed = em.find(User.class, u.id);
            em.remove(managed);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}

