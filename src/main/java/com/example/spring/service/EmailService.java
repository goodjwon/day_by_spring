package com.example.spring.service;

import com.example.spring.entity.Order;

public interface EmailService {
    void sendOrderConfirmation(Order order);
    void sendOrderShipped(Order order);
}