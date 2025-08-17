package com.example.shop.exception;

import java.util.UUID;

public class SupplierNotFoundException extends CustomException {
    private final UUID supplierId;

    public SupplierNotFoundException(UUID supplierId) {
        super("Supplier not found with ID: " + supplierId);
        this.supplierId = supplierId;
    }

    public UUID getSupplierId() {
        return supplierId;
    }
}

// public class SupplierNotFoundException extends RuntimeException {
// private final UUID supplierId;

// public SupplierNotFoundException(UUID supplierId) {
// super("Supplier not found with ID: " + supplierId);
// this.supplierId = supplierId;
// }

// public UUID getSupplierId() {
// return supplierId;
// }
// }