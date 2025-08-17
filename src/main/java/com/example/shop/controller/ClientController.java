package com.example.shop.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.shop.dto.ClientDTO;
import com.example.shop.model.Client;
import com.example.shop.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // 1) Добавление клиента
    @PostMapping("/add")
    @Operation(summary = "Добавление клиента", description = "Создает нового клиента в системе")
    public ResponseEntity<?> addClient(@Valid @RequestBody ClientDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMessages(result));
        }
        Client createdClient = clientService.addClient(dto);
        return ResponseEntity.ok(createdClient);
    }

    // 2) Удаление клиента
    @Operation(summary = "Удаление клиента", description = "Удаление клиента по id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id) {
        if (!clientService.existsById(id)) {
            return ResponseEntity.badRequest().body("Клиент с ID " + id + " не найден");
        }
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }

    // 3) Получение клиентов по имени и фамилии
    @GetMapping("/search")
    @Operation(summary = "Поиск клиента по ФИ", description = "Поиск клиента по имени и фамилии")
    public ResponseEntity<?> getClientsByNameAndSurname(
            @RequestParam @NotBlank String name,
            @RequestParam @NotBlank String surname) {
        List<Client> clients = clientService.getClientsByNameAndSurname(name, surname);
        if (clients.isEmpty()) {
            return ResponseEntity.badRequest().body("Клиенты не найдены");
        }
        return ResponseEntity.ok(clients);
    }

    // 4) Получение всех клиентов (с пагинацией)
    @GetMapping("/all")
    @Operation(summary = "Получить всех клиентов")
    public ResponseEntity<?> getAllClients(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        if (limit != null && limit <= 0) {
            return ResponseEntity.badRequest().body("Limit должен быть > 0");
        }
        if (offset != null && offset < 0) {
            return ResponseEntity.badRequest().body("Offset должен быть >= 0");
        }
        List<Client> clients = clientService.getAllClients(limit, offset);
        return ResponseEntity.ok(clients);
    }

    // 5) Изменение адреса клиента
    @Operation(summary = "Обновить адрес у клиента")
    @PatchMapping("/updateAddress/{id}")
    public ResponseEntity<?> updateClientAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressDTO addressDto) {
        if (!clientService.existsById(id)) {
            return ResponseEntity.badRequest().body("Клиент с ID " + id + " не найден");
        }
        return ResponseEntity.ok(clientService.updateClientAddress(id, addressDto));
    }

    // Вспомогательный метод для форматирования ошибок валидации
    private List<String> getErrorMessages(BindingResult result) {
        return result.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
    }
}