package com.order.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOrderById() throws Exception {
        mockMvc.perform(get("/order/{orderId}", "12345"))
                .andExpect(status().isOk());
    }

    @Test
    void createOrder() throws Exception {
        mockMvc.perform(post("/order/create")
        .contentType("application/json")
                .content("{\n" +
                        "    \"orderName\" : \"Test Order\",\n" +
                        "    \"orderStatus\" : \"ready for shipment\"\n" +
                        "}"))
                .andExpect(status().isCreated());

    }
}
