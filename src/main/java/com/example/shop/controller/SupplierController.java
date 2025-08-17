package com.example.shop.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dto.AddressDTO;
import com.example.shop.dto.SupplierDTO;
import com.example.shop.model.Supplier;
import com.example.shop.service.SupplierService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // 1) Добавление поставщика
    @PostMapping("/add")
    @Operation(summary = "Добавление поставщика", description = "Создает нового поставщика в системе")
    public ResponseEntity<?> addSupplier(@Valid @RequestBody SupplierDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMessages(result));
        }
        return ResponseEntity.ok(supplierService.addSupplier(dto));
    }

    // 2) Изменение адреса поставщика
    @Operation(summary = "Обновить адрес у поставщика")
    @PatchMapping("/updateAddress/{id}")
    public ResponseEntity<?> updateSupplierAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressDTO addressDto) {
        if (!supplierService.existsById(id)) {
            return ResponseEntity.badRequest().body("Клиент с ID " + id + " не найден");
        }
        return ResponseEntity.ok(supplierService.updateSupplierAddress(id, addressDto));
    }

    // 3) Удаление поставщика
    @Operation(summary = "Удаление поставщика", description = "Удаление поставщика по id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }

    // 4) Получение всех поставщиков (с пагинацией)
    @GetMapping("/all")
    @Operation(summary = "Получить всех поставщиков")
    public ResponseEntity<?> getAllSuppliers(
            @RequestParam @Positive Integer limit,
            @RequestParam @PositiveOrZero Integer offset) {

        List<Supplier> suppliers = supplierService.getAll(limit, offset);
        return ResponseEntity.ok(suppliers);
    }

    // 5) Получение поставщика по id
    @GetMapping("/{id}")
    @Operation(summary = "Найти поставщик по id")
    public ResponseEntity<?> getSupplier(
            @PathVariable UUID id) {

        return ResponseEntity.ok(supplierService.getById(id));
    }

    // Вспомогательный метод для форматирования ошибок валидации
    private List<String> getErrorMessages(BindingResult result) {
        return result.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
    }
}