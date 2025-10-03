package com.example.spring.dto.response;

import com.example.spring.entity.Order;
import com.example.spring.entity.OrderItem;
import com.example.spring.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemResponse> items;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .items(order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long bookId;
        private String bookTitle;
        private String bookAuthor;
        private Integer quantity;
        private BigDecimal price;

        public static OrderItemResponse from(OrderItem item) {
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .bookId(item.getBook().getId())
                    .bookTitle(item.getBook().getTitle())
                    .bookAuthor(item.getBook().getAuthor())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
        }
    }
}