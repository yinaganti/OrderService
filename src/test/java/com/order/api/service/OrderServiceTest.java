package com.order.api.service;

import com.order.api.client.KafkaProducer;
import com.order.api.client.ShipmentFeignClient;
import com.order.api.entity.AuditEntity;
import com.order.api.entity.OrderEntity;
import com.order.api.exception.ApplicationException;
import com.order.api.exception.ShipmentException;
import com.order.api.model.OrderRequest;
import com.order.api.repository.jpa.OrderRepository;
import com.order.api.util.AppUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AppUtil appUtil;

    @Mock
    private ShipmentFeignClient shipmentFeignClient;

    @Mock
    private AuditService auditService;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderRequest orderRequest;

    @Mock
    private OrderEntity orderEntity;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private KafkaTemplate kafkaTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(orderService, "orderTopic", "orders");
        ReflectionTestUtils.setField(kafkaProducer, "kafkaTemplate", kafkaTemplate);
    }


    @AfterEach
    void tearDown() {
        orderService.platformThreadExecutor.shutdownNow();
        orderService.virtualThreadExecutor.shutdownNow();
    }

    @Test
    void createOrder_shouldSaveOrderPublishKafkaMessageAndAudit() throws Exception {
       // orderService.orderTopic = "orders";

        when(modelMapper.map(orderRequest, OrderEntity.class))
                .thenReturn(orderEntity);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"orderId\":\"test\"}");

        String orderId = orderService.createOrder(orderRequest);

        assertNotNull(orderId);

        verify(modelMapper).map(orderRequest, OrderEntity.class);
        verify(orderEntity).setOrderId(orderId);
        verify(orderEntity).setOrderCreateTime(any(Timestamp.class));
        verify(orderRepository).save(orderEntity);

        verify(kafkaProducer).send(
                eq("orders"),
                eq(orderId),
                eq("{\"orderId\":\"test\"}")
        );

        verify(auditService).save(any(AuditEntity.class));
    }

    @Test
    void createOrder_whenKafkaFails_shouldThrowShipmentExceptionAndSaveAudit() throws Exception {
        orderService.orderTopic = "orders";

        when(modelMapper.map(orderRequest, OrderEntity.class))
                .thenReturn(orderEntity);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"orderId\":\"test\"}");

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(kafkaProducer)
                .send(anyString(), anyString(), anyString());

        ShipmentException exception = assertThrows(
                ShipmentException.class,
                () -> orderService.createOrder(orderRequest)
        );

        assertEquals("Kafka unavailable", exception.getMessage());

        verify(orderRepository).save(orderEntity);
        verify(auditService).save(any(AuditEntity.class));
    }

    @Test
    void createOrderByPlatform_shouldSaveOrderAndReturnOrderId()
            throws ExecutionException, InterruptedException {

        when(modelMapper.map(orderRequest, OrderEntity.class))
                .thenReturn(orderEntity);

        String orderId = orderService.createOrderByPlatform(orderRequest);

        assertNotNull(orderId);

        verify(modelMapper).map(orderRequest, OrderEntity.class);
        verify(orderEntity).setOrderId(orderId);
        verify(orderEntity).setOrderCreateTime(any(Timestamp.class));
        verify(orderRepository).save(orderEntity);
    }

    @Test
    void createOrderByVirtual_shouldSaveOrderAndReturnOrderId()
            throws ExecutionException, InterruptedException {

        when(modelMapper.map(orderRequest, OrderEntity.class))
                .thenReturn(orderEntity);

        String orderId = orderService.createOrderByVirtual(orderRequest);

        assertNotNull(orderId);

        verify(modelMapper).map(orderRequest, OrderEntity.class);
        verify(orderEntity).setOrderId(orderId);
        verify(orderEntity).setOrderCreateTime(any(Timestamp.class));
        verify(orderRepository).save(orderEntity);
    }

    @Test
    public void createOrderWithExc() throws ApplicationException, ShipmentException {
        when(modelMapper.map(orderRequest, OrderEntity.class))
                .thenReturn(orderEntity);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"orderId\":\"test\"}");

        doThrow(new RuntimeException("Kafka unavailable")).when(kafkaProducer).send(anyString(), anyString(), anyString());
        assertThrows(ShipmentException.class, () -> orderService.createOrder(orderRequest));

    }
}