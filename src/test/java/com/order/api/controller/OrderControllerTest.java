package com.order.api.controller;

import com.order.api.domain.MockData;
import com.order.api.model.OrderRequest;
import com.order.api.model.OrderResponse;
import com.order.api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderController(orderService);
    }

    @Test
    void createOrderReturnsOkWithOrderId() throws Exception {
        when(orderService.createOrder(any())).thenReturn("order-123");
        ResponseEntity<String> response = controller.createOrder(MockData.getOrderRequest());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("order-123", response.getBody());
        verify(orderService, times(1)).createOrder(MockData.getOrderRequest());
    }

    @Test
    void createOrderByPlatformReturnsCreatedWithOrderId() throws Exception {
        when(orderService.createOrderByPlatform(MockData.getOrderRequest())).thenReturn("order-456");
        ResponseEntity<String> response = controller.createOrderByPlatform(MockData.getOrderRequest());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("order-456", response.getBody());
        verify(orderService).createOrderByPlatform(MockData.getOrderRequest());
    }

    @Test
    void createOrderByVirtualReturnsOkWithOrderId() throws Exception {
        when(orderService.createOrderByVirtual(MockData.getOrderRequest())).thenReturn("order-789");

        ResponseEntity<String> response = controller.createOrderByVirtual(MockData.getOrderRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("order-789", response.getBody());
        verify(orderService).createOrderByVirtual(MockData.getOrderRequest());
    }
}