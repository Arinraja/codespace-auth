package com.oms.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long customerId;
    private static Long productId;
    private static Long orderId;

    @Test
    @Order(1)
    @DisplayName("POST /api/customers — create customer")
    void createCustomer() throws Exception {
        String body = """
                {
                  "name": "Asha Sharma",
                  "email": "asha@example.com",
                  "phone": "9876543210"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Asha Sharma")))
                .andExpect(jsonPath("$.email", is("asha@example.com")))
                .andReturn();

        customerId = readId(result);
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/products — create product")
    void createProduct() throws Exception {
        String body = """
                {
                  "name": "Wireless Mouse",
                  "description": "Ergonomic mouse",
                  "price": 799.00,
                  "stockQuantity": 50
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Wireless Mouse")))
                .andExpect(jsonPath("$.price", is(799.00)))
                .andReturn();

        productId = readId(result);
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/orders — create order")
    void createOrder() throws Exception {
        String body = """
                {
                  "customerId": %d
                }
                """.formatted(customerId);

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is(customerId.intValue())))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.totalAmount", is(0)))
                .andReturn();

        orderId = readId(result);
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/orders/{id}/products — add product to order")
    void addProductToOrder() throws Exception {
        String body = """
                {
                  "productId": %d,
                  "quantity": 2
                }
                """.formatted(productId);

        mockMvc.perform(post("/api/orders/{orderId}/products", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity", is(2)))
                .andExpect(jsonPath("$.totalAmount", is(1598.00)));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/orders/{id} — get order details")
    void getOrderDetails() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())))
                .andExpect(jsonPath("$.customerName", is("Asha Sharma")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.status", is("CREATED")));
    }

    @Test
    @Order(6)
    @DisplayName("PUT /api/orders/{id}/cancel — cancel order")
    void cancelOrder() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        // Stock should be restored
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity", is(50)));
    }

    @Test
    @Order(7)
    @DisplayName("GET unknown order — returns 404")
    void getUnknownOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    private Long readId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
