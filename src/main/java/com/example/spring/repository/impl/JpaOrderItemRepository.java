package com.example.spring.repository.impl;

import com.example.spring.entity.OrderItem;
import com.example.spring.repository.OrderItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaOrderItemRepository implements OrderItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<OrderItem> findById(Long id) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        return Optional.ofNullable(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> findAll() {
        return em.createQuery("SELECT oi FROM OrderItem oi", OrderItem.class)
                .getResultList();
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            em.persist(orderItem);
            return orderItem;
        } else {
            return em.merge(orderItem);
        }
    }

    @Override
    public void deleteById(Long id) {
        OrderItem orderItem = em.find(OrderItem.class, id);
        if (orderItem != null) {
            em.remove(orderItem);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> findByOrderId(Long orderId) {
        return em.createQuery(
                "SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId", 
                OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> findByBookId(Long bookId) {
        return em.createQuery(
                "SELECT oi FROM OrderItem oi WHERE oi.book.id = :bookId", 
                OrderItem.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> findByOrderIdAndBookId(Long orderId, Long bookId) {
        return em.createQuery(
                "SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.book.id = :bookId", 
                OrderItem.class)
                .setParameter("orderId", orderId)
                .setParameter("bookId", bookId)
                .getResultList();
    }
}