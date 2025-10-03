package com.example.spring.controller;

import com.example.spring.dto.CreateOrderRequest;
import com.example.spring.dto.response.OrderResponse;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderStatus;
import com.example.spring.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@DisplayName("OrderController 통합 테스트")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id(1L)
                .totalAmount(new BigDecimal("45000"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        createOrderRequest = CreateOrderRequest.builder()
                .bookIds(Arrays.asList(1L, 2L))
                .customerEmail("test@example.com")
                .build();
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrderTest {

        @Test
        @DisplayName("주문 생성 성공")
        void createOrder_유효한요청_생성성공() throws Exception {
            // Given
            given(orderService.createOrder(anyList())).willReturn(testOrder);

            // When & Then
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createOrderRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.totalAmount").value(45000))
                    .andExpect(jsonPath("$.status").value("PENDING"));

            verify(orderService).createOrder(anyList());
        }

        @Test
        @DisplayName("빈 도서 목록으로 주문 생성")
        void createOrder_빈도서목록_생성성공() throws Exception {
            // Given
            CreateOrderRequest emptyRequest = CreateOrderRequest.builder()
                    .bookIds(new ArrayList<>())
                    .customerEmail("test@example.com")
                    .build();

            Order emptyOrder = Order.builder()
                    .id(1L)
                    .totalAmount(BigDecimal.ZERO)
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .orderItems(new ArrayList<>())
                    .build();

            given(orderService.createOrder(anyList())).willReturn(emptyOrder);

            // When & Then
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.totalAmount").value(0));

            verify(orderService).createOrder(anyList());
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class GetOrderTest {

        @Test
        @DisplayName("주문 단건 조회 성공")
        void getOrder_존재하는주문_조회성공() throws Exception {
            // Given
            given(orderService.findOrderById(1L)).willReturn(testOrder);

            // When & Then
            mockMvc.perform(get("/api/orders/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.totalAmount").value(45000))
                    .andExpect(jsonPath("$.status").value("PENDING"));

            verify(orderService).findOrderById(1L);
        }

        @Test
        @DisplayName("주문 목록 조회 - 페이징")
        void getAllOrders_페이징_조회성공() throws Exception {
            // Given
            List<Order> orders = Arrays.asList(testOrder);
            Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);

            given(orderService.findAllOrders(any(Pageable.class))).willReturn(orderPage);

            // When & Then
            mockMvc.perform(get("/api/orders")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(orderService).findAllOrders(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("주문 상태 관리")
    class OrderStatusTest {

        @Test
        @DisplayName("주문 상태 변경 성공")
        void updateOrderStatus_유효한상태_변경성공() throws Exception {
            // Given
            Order updatedOrder = Order.builder()
                    .id(1L)
                    .totalAmount(new BigDecimal("45000"))
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.CONFIRMED)
                    .orderItems(new ArrayList<>())
                    .build();

            given(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)).willReturn(updatedOrder);

            // When & Then
            mockMvc.perform(patch("/api/orders/1/status")
                            .param("status", "CONFIRMED"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.status").value("CONFIRMED"));

            verify(orderService).updateOrderStatus(1L, OrderStatus.CONFIRMED);
        }

        @Test
        @DisplayName("상태별 주문 조회")
        void getOrdersByStatus_상태지정_조회성공() throws Exception {
            // Given
            List<Order> orders = Arrays.asList(testOrder);
            Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);

            given(orderService.findOrdersByStatus(eq(OrderStatus.PENDING), any(Pageable.class))).willReturn(orderPage);

            // When & Then
            mockMvc.perform(get("/api/orders/status/PENDING")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].status").value("PENDING"));

            verify(orderService).findOrdersByStatus(eq(OrderStatus.PENDING), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("주문 수정")
    class UpdateOrderTest {

        @Test
        @DisplayName("주문 수정 성공")
        void updateOrder_유효한요청_수정성공() throws Exception {
            // Given
            given(orderService.updateOrder(eq(1L), anyList())).willReturn(testOrder);

            // When & Then
            mockMvc.perform(put("/api/orders/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createOrderRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(orderService).updateOrder(eq(1L), anyList());
        }
    }

    @Nested
    @DisplayName("주문 취소")
    class CancelOrderTest {

        @Test
        @DisplayName("주문 취소 성공")
        void cancelOrder_유효한주문_취소성공() throws Exception {
            // Given
            Order cancelledOrder = Order.builder()
                    .id(1L)
                    .totalAmount(new BigDecimal("45000"))
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.CANCELLED)
                    .orderItems(new ArrayList<>())
                    .build();

            given(orderService.cancelOrder(1L)).willReturn(cancelledOrder);

            // When & Then
            mockMvc.perform(post("/api/orders/1/cancel"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.status").value("CANCELLED"));

            verify(orderService).cancelOrder(1L);
        }
    }

    @Nested
    @DisplayName("주문 통계")
    class OrderStatisticsTest {

        @Test
        @DisplayName("주문 통계 조회 성공")
        void getOrderStatistics_통계조회_성공() throws Exception {
            // Given
            given(orderService.getTotalOrdersCount()).willReturn(100L);
            given(orderService.getOrdersCountByStatus(OrderStatus.PENDING)).willReturn(20L);
            given(orderService.getOrdersCountByStatus(OrderStatus.CONFIRMED)).willReturn(30L);
            given(orderService.getOrdersCountByStatus(OrderStatus.SHIPPED)).willReturn(25L);
            given(orderService.getOrdersCountByStatus(OrderStatus.DELIVERED)).willReturn(20L);
            given(orderService.getOrdersCountByStatus(OrderStatus.CANCELLED)).willReturn(5L);
            given(orderService.getTotalRevenue()).willReturn(new BigDecimal("5000000"));
            given(orderService.getAverageOrderAmount()).willReturn(new BigDecimal("50000"));

            // When & Then
            mockMvc.perform(get("/api/orders/statistics"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalOrders").value(100))
                    .andExpect(jsonPath("$.pendingOrders").value(20))
                    .andExpect(jsonPath("$.confirmedOrders").value(30))
                    .andExpect(jsonPath("$.totalRevenue").value(5000000))
                    .andExpect(jsonPath("$.averageOrderAmount").value(50000));
        }

        @Test
        @DisplayName("기간별 주문 조회")
        void getOrdersByPeriod_기간지정_조회성공() throws Exception {
            // Given
            List<Order> orders = Arrays.asList(testOrder);
            Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);

            given(orderService.findOrdersByPeriod(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                    .willReturn(orderPage);

            // When & Then
            mockMvc.perform(get("/api/orders/period")
                            .param("startDate", "2025-01-01T00:00:00")
                            .param("endDate", "2025-12-31T23:59:59")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(orderService).findOrdersByPeriod(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        }
    }
}
