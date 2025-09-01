package com.example.shop.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dto.ProductDTO;
import com.example.shop.model.Product;
import com.example.shop.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1) Добавление продукта
    @Operation(summary = "Добавление продукта", description = "Добавляет новый продукт")
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductDTO productDto) {
        return ResponseEntity.ok(productService.addProduct(productDto));
    }

    // 2) Уменьшение количества товара
    @Operation(summary = "Уменьшение количества товара")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Продукт с таким ID не найден"),
            @ApiResponse(responseCode = "409", description = "Недостаточно товара для уменьшения количества")
    })
    @PostMapping("/decrease/{id}")
    public ResponseEntity<Product> reductionOfProduct(
            @PathVariable UUID id,
            @RequestParam @Positive long count) {

        Product updatedProduct = productService.decreaseAmountProduct(id, count);
        return ResponseEntity.ok(updatedProduct);
    }

    // 3) Поиск товара по id
    @Operation(summary = "Поиск товара по id")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Продукт с таким ID не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // 4) Поиск всех товаров
    @Operation(summary = "Поиск всех товаров")
    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 5) Удаление товара
    @Operation(summary = "Удаление товара")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Продукт с таким ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}