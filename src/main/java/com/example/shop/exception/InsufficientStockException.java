package com.example.shop.exception;

import java.util.UUID;

public class InsufficientStockException extends CustomException {
    private final UUID productId;
    private final long available;
    private final long requested;

    public InsufficientStockException(UUID productId, long available, long requested) {
        super(String.format(
                "Insufficient stock for product %s: available %d, requested %d",
                productId, available, requested));
        this.productId = productId;
        this.available = available;
        this.requested = requested;
    }

    // Геттеры
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