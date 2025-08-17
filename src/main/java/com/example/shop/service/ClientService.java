package com.example.shop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.shop.dto.AddressDTO;
import com.example.shop.dto.ClientDTO;
import com.example.shop.mapper.AddressMapper;
import com.example.shop.mapper.ClientMapper;
import com.example.shop.model.Client;
import com.example.shop.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AddressMapper addressMapper;

    // 1) Добавление клиента
    public Client addClient(ClientDTO clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        return clientRepository.save(client);
    }

    // 2) Удаление клиента
    public void deleteClient(UUID id) {
        clientRepository.deleteById(id);
    }

    // 3) Поиск по имени и фамилии
    public List<Client> getClientsByNameAndSurname(String name, String surname) {
        return clientRepository.findByNameAndSurname(name, surname);
    }

    // 4) Получение всех клиентов (с пагинацией)
    public List<Client> getAllClients(Integer limit, Integer offset) {
        if (limit != null && offset != null) {
            Pageable pageable = PageRequest.of(offset, limit);
            Page<Client> page = clientRepository.findAll(pageable);
            return page.getContent();
        }
        // @TODO null ???
        return clientRepository.findAll();
    }

    // 5) Обновление адреса клиента
    public Client updateClientAddress(UUID id, AddressDTO addressDTO) {
        Client client = clientRepository.findById(id).orElseThrow();

        client.setAddress(addressMapper.toEntity(addressDTO));

        return clientRepository.save(client);
    }

    public boolean existsById(UUID id) {
        return clientRepository.existsById(id);
    }
}