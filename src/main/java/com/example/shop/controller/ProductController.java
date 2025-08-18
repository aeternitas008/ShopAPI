package com.example.shop.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dto.ProductDTO;
import com.example.shop.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1) Добавление клиента
    @Operation(summary = "Добавление продукта", description = "Добавляет новый продукт")
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductDTO productDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMessages(result));
        }

        return ResponseEntity.ok(productService.addProduct(productDto));
    }

    // 2) Уменьшение количества товара
    @Operation(summary = "Уменьшение количества товара")
    @PostMapping("/decrease/{id}")
    public ResponseEntity<?> reductionOfProduct(@PathVariable UUID productId, @PathVariable @Positive long count) {
        if (!productService.existsById(productId)) {
            return ResponseEntity.badRequest().body("Клиент с ID " + productId + " не найден");
        }

        return ResponseEntity.ok(productService.decreaseAmountProduct(productId, count));
    }

    // 3) Поиск товара по id
    @Operation(summary = "Поиск товара по id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findProductById(@PathVariable UUID productId) {
        if (!productService.existsById(productId)) {
            return ResponseEntity.badRequest().body("Клиент с ID " + productId + " не найден");
        }
        return ResponseEntity.ok(productService.findById(productId));
    }

    // 4) Поиск всех товаров
    @Operation(summary = "Поиск всех товаров")
    @GetMapping("/all")
    public ResponseEntity<?> findAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 5) Удаление товара
    @Operation(summary = "Удаление товара")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId) {
        if (!productService.existsById(productId)) {
            return ResponseEntity.badRequest().body("Клиент с ID " + productId + " не найден");
        }
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    // Вспомогательный метод для форматирования ошибок валидации
    private List<String> getErrorMessages(BindingResult result) {
        return result.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
    }
}