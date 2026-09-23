package com.oms.dto;

import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
