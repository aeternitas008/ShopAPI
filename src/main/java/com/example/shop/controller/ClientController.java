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
import com.example.shop.dto.ClientDTO;
import com.example.shop.exception.BadRequestException;
import com.example.shop.model.Client;
import com.example.shop.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // 1) Добавление клиента
    @PostMapping("/add")
    @Operation(summary = "Добавление клиента", description = "Создает нового клиента в системе")
    public ResponseEntity<Client> addClient(@RequestBody @Valid ClientDTO dto) {
        Client createdClient = clientService.addClient(dto);
        return ResponseEntity.ok(createdClient);
    }

    // 2) Удаление клиента
    @Operation(summary = "Удаление клиента", description = "Удаление клиента по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.existsById(id);
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }

    // 3) Получение клиентов по имени и фамилии
    @GetMapping("/search")
    @Operation(summary = "Поиск клиента по ФИ", description = "Поиск клиента по имени и фамилии")
    public ResponseEntity<List<Client>> getClientsByNameAndSurname(
            @RequestParam @Valid @NotBlank String name,
            @RequestParam @Valid @NotBlank String surname) {

        List<Client> clients = clientService.getClientsByNameAndSurname(name, surname);

        return ResponseEntity.ok(clients);
    }

    // 4) Получение всех клиентов (с пагинацией)
    @GetMapping("/all")
    @Operation(summary = "Получить всех клиентов")
    public ResponseEntity<List<Client>> getAllClients(
            @RequestParam(required = false) @Valid Integer limit,
            @RequestParam(required = false) @Valid Integer offset) {

        if (limit != null && limit <= 0) {
            throw new BadRequestException("Limit должен быть > 0");
        }
        if (offset != null && offset < 0) {
            throw new BadRequestException("Offset должен быть >= 0");
        }

        List<Client> clients = clientService.getAllClients(limit, offset);
        return ResponseEntity.ok(clients);
    }

    // 5) Изменение адреса клиента
    @Operation(summary = "Обновить адрес у клиента")
    @PatchMapping("/update-address/{id}")
    public ResponseEntity<Client> updateClientAddress(
            @PathVariable UUID id,
            @RequestBody @Valid AddressDTO addressDto) {

        return ResponseEntity.ok(clientService.updateClientAddress(id, addressDto));
    }

    // 6) Получение клиента по ID
    @GetMapping("/{id}")
    @Operation(summary = "Получение клиента по ID")
    public ResponseEntity<Client> getClientById(@PathVariable UUID id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(client);
    }
}