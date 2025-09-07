package com.example.shop.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException forClient(UUID id) {
        return new NotFoundException("Клиент с ID " + id + " не найден");
    }

    public static NotFoundException forImage(UUID id) {
        return new NotFoundException("Изображение с ID " + id + " не найдено");
    }

    public static NotFoundException forProduct(UUID id) {
        return new NotFoundException("Товар с ID " + id + " не найден");
    }

    public static NotFoundException forSupplier(UUID id) {
        return new NotFoundException("Поставщик с ID " + id + " не найден");
    }

}