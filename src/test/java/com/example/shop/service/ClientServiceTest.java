package com.example.shop.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.shop.dto.AddressDTO;
import com.example.shop.dto.ClientDTO;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.AddressMapper;
import com.example.shop.mapper.ClientMapper;
import com.example.shop.model.Client;
import com.example.shop.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private ClientService clientService;

    private final UUID existingClientId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingClientId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    private ClientDTO createValidClientDTO() {
        ClientDTO dto = new ClientDTO();
        dto.setName("Иван");
        dto.setSurname("Иванов");
        dto.setBirthday(LocalDate.of(1990, 1, 1));
        dto.setGender("M");
        return dto;
    }

    private Client createClientEntity() {
        Client client = new Client();
        client.setId(existingClientId);
        client.setName("Иван");
        client.setSurname("Иванов");
        client.setBirthday(LocalDate.of(1990, 1, 1));
        client.setGender("M");
        return client;
    }

    private AddressDTO createValidAddressDTO() {
        AddressDTO dto = new AddressDTO();
        dto.setCountry("Россия");
        dto.setCity("Москва");
        dto.setStreet("Тверская");
        dto.setHouse("1");
        return dto;
    }

    @Test
    void addClient_WithValidData_ShouldSaveAndReturnClient() {
        // given
        ClientDTO clientDTO = createValidClientDTO();
        Client clientEntity = createClientEntity();

        when(clientMapper.toEntity(clientDTO)).thenReturn(clientEntity);
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);

        // when
        Client result = clientService.addClient(clientDTO);

        // then
        assertNotNull(result);
        assertEquals(existingClientId, result.getId());
        assertEquals("Иван", result.getName());
        assertEquals("Иванов", result.getSurname());

        verify(clientMapper, times(1)).toEntity(clientDTO);
        verify(clientRepository, times(1)).save(clientEntity);
    }

    @Test
    void deleteClient_WithExistingId_ShouldCallRepositoryDelete() {
        // given
        doNothing().when(clientRepository).deleteById(existingClientId);

        // when
        clientService.deleteClient(existingClientId);

        // then
        verify(clientRepository, times(1)).deleteById(existingClientId);
    }

    @Test
    void getClientsByNameAndSurname_ShouldReturnClientsList() {
        // given
        String name = "Иван";
        String surname = "Иванов";
        List<Client> clients = List.of(createClientEntity());

        when(clientRepository.findByNameAndSurname(name, surname)).thenReturn(clients);

        // when
        List<Client> result = clientService.getClientsByNameAndSurname(name, surname);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getName());

        verify(clientRepository, times(1)).findByNameAndSurname(name, surname);
    }

    @Test
    void getAllClients_WithPagination_ShouldReturnPagedClients() {
        // given
        int limit = 10;
        int offset = 0;
        Pageable pageable = PageRequest.of(offset, limit);
        List<Client> clients = List.of(createClientEntity());
        Page<Client> page = new PageImpl<>(clients);

        when(clientRepository.findAll(pageable)).thenReturn(page);

        // when
        List<Client> result = clientService.getAllClients(limit, offset);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(clientRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllClients_WithoutPagination_ShouldReturnAllClients() {
        // given
        List<Client> clients = List.of(createClientEntity());

        when(clientRepository.findAll()).thenReturn(clients);

        // when
        List<Client> result = clientService.getAllClients(null, null);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(clientRepository, times(1)).findAll();
        verify(clientRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void updateClientAddress_WithExistingClient_ShouldUpdateAddress() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();
        Client existingClient = createClientEntity();
        Client updatedClient = createClientEntity();

        when(clientRepository.findById(existingClientId)).thenReturn(Optional.of(existingClient));
        when(addressMapper.toEntity(addressDTO)).thenReturn(new com.example.shop.model.Address());
        when(clientRepository.save(existingClient)).thenReturn(updatedClient);

        // when
        Client result = clientService.updateClientAddress(existingClientId, addressDTO);

        // then
        assertNotNull(result);

        verify(clientRepository, times(1)).findById(existingClientId);
        verify(addressMapper, times(1)).toEntity(addressDTO);
        verify(clientRepository, times(1)).save(existingClient);
    }

    @Test
    void updateClientAddress_WithNonExistingClient_ShouldThrowException() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();

        when(clientRepository.findById(nonExistingClientId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            clientService.updateClientAddress(nonExistingClientId, addressDTO);
        });

        verify(clientRepository, times(1)).findById(nonExistingClientId);
        verify(addressMapper, never()).toEntity(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void getClientById_WithExistingClient_ShouldReturnClient() {
        // given
        Client client = createClientEntity();

        when(clientRepository.findById(existingClientId)).thenReturn(Optional.of(client));

        // when
        Client result = clientService.getClientById(existingClientId);

        // then
        assertNotNull(result);
        assertEquals(existingClientId, result.getId());

        verify(clientRepository, times(1)).findById(existingClientId);
    }

    @Test
    void getClientById_WithNonExistingClient_ShouldThrowNotFoundException() {
        // given
        when(clientRepository.findById(nonExistingClientId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            clientService.getClientById(nonExistingClientId);
        });

        verify(clientRepository, times(1)).findById(nonExistingClientId);
    }

    @Test
    void existsById_WithExistingClient_ShouldNotThrowException() {
        // given
        when(clientRepository.existsById(existingClientId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> {
            clientService.existsById(existingClientId);
        });

        verify(clientRepository, times(1)).existsById(existingClientId);
    }

    @Test
    void existsById_WithNonExistingClient_ShouldThrowNotFoundException() {
        // given
        when(clientRepository.existsById(nonExistingClientId)).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            clientService.existsById(nonExistingClientId);
        });

        verify(clientRepository, times(1)).existsById(nonExistingClientId);
    }
}