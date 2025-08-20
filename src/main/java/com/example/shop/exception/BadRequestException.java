// src/main/java/com/example/shop/exception/BadRequestException.java
package com.example.shop.exception;

import java.util.UUID;

public class BadRequestException extends RuntimeException {

    private UUID productId;
    private long available;
    private long requested;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(UUID productId, long available, long requested) {
        super(String.format(
                "Insufficient stock for product %s: available %d, requested %d",
                productId, available, requested));
        this.productId = productId;
        this.available = available;
        this.requested = requested;
    }

    public UUID getProductId() {
        return productId;
    }

    public long getAvailable() {
        return available;
    }

    public long getRequested() {
        return requested;
    }
}