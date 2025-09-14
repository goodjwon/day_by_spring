package com.example.patten.observer.after;

import lombok.Data;

@Data
public class OrderPlacedEvent {
    private final String productId;
    private final String address;
    private final String orderId;

    public OrderPlacedEvent(String productId, String address, String orderId) {
        this.productId = productId;
        this.address = address;
        this.orderId = orderId;
    }
}