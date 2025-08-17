package com.example.shop.exception;

import java.util.UUID;

public class ProductNotFoundException extends CustomException {
    private final UUID productId;

    public ProductNotFoundException(UUID productId) {
        super("Product not found with ID: " + productId);
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
}