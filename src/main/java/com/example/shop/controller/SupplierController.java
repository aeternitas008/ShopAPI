package com.example.shop.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // 1) Добавление поставщика
    @PostMapping("/add")
    @Operation(summary = "Добавление поставщика", description = "Создает нового поставщика в системе")
    public ResponseEntity<Supplier> addSupplier(@Valid @RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.addSupplier(dto));
    }

    // 2) Изменение адреса поставщика
    @Operation(summary = "Обновить адрес у поставщика")
    @PatchMapping("/updateAddress/{id}")
    public ResponseEntity<Supplier> updateSupplierAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressDTO addressDto) {

        supplierService.existsById(id);
        return ResponseEntity.ok(supplierService.updateSupplierAddress(id, addressDto));
    }

    // 3) Удаление поставщика
    @Operation(summary = "Удаление поставщика", description = "Удаление поставщика по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        supplierService.existsById(id);
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }

    // 4) Получение всех поставщиков (с пагинацией)
    @GetMapping("/all")
    @Operation(summary = "Получить всех поставщиков")
    public ResponseEntity<List<Supplier>> getAllSuppliers(
            @RequestParam(required = false) @Positive Integer limit,
            @RequestParam(required = false) @PositiveOrZero Integer offset) {

        List<Supplier> suppliers = supplierService.getAll(limit, offset);
        return ResponseEntity.ok(suppliers);
    }

    // 5) Поиск поставщика по id
    @GetMapping("/{id}")
    @Operation(summary = "Поиск поставщика по id")
    public ResponseEntity<Supplier> getSupplier(@PathVariable UUID id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }
}