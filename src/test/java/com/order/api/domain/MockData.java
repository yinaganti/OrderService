package com.order.api.domain;

import com.order.api.model.OrderRequest;

public class MockData {

    public static OrderRequest getOrderRequest() {
        return OrderRequest.builder()
                .orderName("Test Order")
                .orderStatus("CREATED")
                .build();
    }
}
