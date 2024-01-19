package com.example.mqtt.repository;


import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
 
@Repository
public class GenericRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> List<T> findAll(Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public <T, ID> T findById(Class<T> entityClass, ID id) {
        return entityManager.find(entityClass, id);
    }

    public <T> void save(T entity) {
        entityManager.persist(entity);
    }

    // Các phương thức CRUD khác

}