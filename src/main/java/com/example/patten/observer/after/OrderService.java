package com.example.patten.observer.after;

// Spring Bean으로 등록될 클래스들

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

// 이벤트 발행자 (주체)
@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void placeOrder(String productId, String address, String orderId) {
        System.out.println("--- 주문 처리를 시작합니다 ---");
        System.out.println("주문이 성공적으로 완료되었습니다.");

        // 주문 완료 '이벤트'를 발행!
        // OrderService는 이제 재고, 배송 서비스를 전혀 모릅니다.
        System.out.println(">> 'OrderPlacedEvent'를 발행합니다...");
        eventPublisher.publishEvent(new OrderPlacedEvent(productId, address, orderId));
        System.out.println("---------------------------\n");
    }
}

// 이벤트 구독자 1 (관찰자)
@Service
class InventoryService {
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        System.out.println("⭐️ [재고] 'OrderPlacedEvent' 수신!");
        System.out.println("상품 ID: " + event.getProductId() + "의 재고를 차감합니다.");
    }
}

// 이벤트 구독자 2 (관찰자)
@Service
class ShippingService {
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        System.out.println("⭐️ [배송] 'OrderPlacedEvent' 수신!");
        System.out.println("주소: " + event.getAddress() + "로 배송을 준비합니다.");
    }
}

@Service
class CouponService {
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        System.out.println("⭐️ [쿠폰] 'CouponEvent' 수신!");
        System.out.println("주소: " + event.getOrderId() + "로 코폰을 발행 합니다.");
    }
}