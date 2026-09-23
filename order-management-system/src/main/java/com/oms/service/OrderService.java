package com.oms.service;

import com.oms.dto.AddProductRequest;
import com.oms.dto.OrderItemResponse;
import com.oms.dto.OrderRequest;
import com.oms.dto.OrderResponse;
import com.oms.entity.Customer;
import com.oms.entity.Order;
import com.oms.entity.OrderItem;
import com.oms.entity.OrderStatus;
import com.oms.entity.Product;
import com.oms.exception.BadRequestException;
import com.oms.exception.ResourceNotFoundException;
import com.oms.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        CustomerService customerService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.productService = productService;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerService.findCustomerOrThrow(request.getCustomerId());
        Order order = new Order(customer);
        Order saved = orderRepository.save(order);
        log.info("Created order id={} for customer id={}", saved.getId(), customer.getId());
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse addProductToOrder(Long orderId, AddProductRequest request) {
        Order order = findOrderOrThrow(orderId);
        ensureOrderModifiable(order);

        Product product = productService.findProductOrThrow(request.getProductId());
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException(
                    "Insufficient stock for product id=" + product.getId()
                            + ". Available=" + product.getStockQuantity());
        }

        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());

        OrderItem item = new OrderItem(order, product, request.getQuantity(), product.getPrice());
        order.addItem(item);

        Order saved = orderRepository.save(order);
        log.info("Added product id={} qty={} to order id={}", product.getId(), request.getQuantity(), orderId);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long orderId) {
        return toResponse(findOrderOrThrow(orderId));
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled: " + orderId);
        }

        // Restore stock for each line item
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("Cancelled order id={}", orderId);
        return toResponse(saved);
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    private void ensureOrderModifiable(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot modify a cancelled order: " + order.getId());
        }
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getName());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .toList());
        return response;
    }
}
