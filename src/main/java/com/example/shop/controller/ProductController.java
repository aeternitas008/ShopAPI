package com.example.shop.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dto.ProductDTO;
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Product;
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

    // 1) Добавление продукта
    @Operation(summary = "Добавление продукта", description = "Добавляет новый продукт")
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductDTO productDto) {
        return ResponseEntity.ok(productService.addProduct(productDto));
    }

    // 2) Уменьшение количества товара
    @Operation(summary = "Уменьшение количества товара")
    @PostMapping("/decrease/{id}")
    public ResponseEntity<Product> reductionOfProduct(
            @PathVariable UUID id,
            @RequestParam @Valid @Positive long count) {

        Product updatedProduct = productService.decreaseAmountProductValidated(id, count);
        return ResponseEntity.ok(updatedProduct);
    }

    // 3) Поиск товара по id
    @Operation(summary = "Поиск товара по id")
    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 4) Поиск всех товаров
    @Operation(summary = "Поиск всех товаров")
    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            throw new NotFoundException("Товары не найдены");
        }
        return ResponseEntity.ok(products);
    }

    // 5) Удаление товара
    @Operation(summary = "Удаление товара")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.existsById(id);
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}