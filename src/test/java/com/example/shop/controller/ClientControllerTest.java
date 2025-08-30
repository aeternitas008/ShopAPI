package com.example.shop.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shop.dto.AddressDTO;
import com.example.shop.dto.ClientDTO;
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Address;
import com.example.shop.model.Client;
import com.example.shop.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClientController.class)
@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID existingClientId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingClientId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    // Вспомогательные методы для создания тестовых данных
    private ClientDTO createValidClientDTO() {
        ClientDTO dto = new ClientDTO();
        dto.setName("Иван");
        dto.setSurname("Иванов");
        dto.setBirthday(LocalDate.of(1990, 1, 1));
        dto.setGender("M");
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setAddressDto(createValidAddressDTO());
        return dto;
    }

    private ClientDTO createInvalidClientDTO() {
        ClientDTO dto = new ClientDTO();
        dto.setName(""); // Невалидное пустое имя
        dto.setSurname(""); // Невалидная пустая фамилия
        dto.setBirthday(null); // Невалидная дата рождения
        return dto;
    }

    private Client createClientEntity() {
        Client client = new Client();
        client.setId(existingClientId);
        client.setName("Иван");
        client.setSurname("Иванов");
        client.setBirthday(LocalDate.of(1990, 1, 1));
        client.setGender("M");
        client.setRegistrationDate(LocalDateTime.now());
        client.setAddress(createAddressEntity());
        return client;
    }

    private Address createAddressEntity() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setCountry("Россия");
        address.setCity("Москва");
        address.setStreet("Тверская");
        address.setHouse("1");
        return address;
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
    void addClient_WithValidData_Returns200() throws Exception {

        ClientDTO validDto = createValidClientDTO();
        Client createdClient = createClientEntity();

        when(clientService.addClient(any(ClientDTO.class))).thenReturn(createdClient);

        mockMvc.perform(post("/api/v1/client/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingClientId.toString()))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.surname").value("Иванов"));

        verify(clientService, times(1)).addClient(any(ClientDTO.class));
    }

    @Test
    void addClient_WithInvalidData_Returns400() throws Exception {

        ClientDTO invalidDto = createInvalidClientDTO();

        mockMvc.perform(post("/api/v1/client/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(greaterThan(0)))); // Ожидаем список ошибок

        verify(clientService, never()).addClient(any(ClientDTO.class));
    }

    @Test
    void addClient_WithServiceException_Returns500() throws Exception {

        ClientDTO validDto = createValidClientDTO();

        when(clientService.addClient(any(ClientDTO.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/api/v1/client/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isInternalServerError());

        verify(clientService, times(1)).addClient(any(ClientDTO.class));
    }

    @Test
    void deleteClient_WithExistingId_Returns200() throws Exception {
        doNothing().when(clientService).existsById(existingClientId);
        doNothing().when(clientService).deleteClient(existingClientId);

        mockMvc.perform(delete("/api/v1/client/{id}", existingClientId))
                .andExpect(status().isOk());

        verify(clientService, times(1)).existsById(existingClientId);
        verify(clientService, times(1)).deleteClient(existingClientId);
    }

    @Test
    void deleteClient_WithInvalidUuidFormat_Returns400() throws Exception {
        mockMvc.perform(delete("/api/v1/client/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).existsById(any(UUID.class));
        verify(clientService, never()).deleteClient(any(UUID.class));
    }

    @Test
    void getClientsByNameAndSurname_WithExistingClients_Returns200() throws Exception {

        String name = "Иван";
        String surname = "Иванов";
        List<Client> clients = List.of(createClientEntity());

        when(clientService.getClientsByNameAndSurname(name, surname)).thenReturn(clients);

        mockMvc.perform(get("/api/v1/client/search")
                .param("name", name)
                .param("surname", surname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].surname").value(surname));

        verify(clientService, times(1)).getClientsByNameAndSurname(name, surname);
    }

    @Test
    void getClientsByNameAndSurname_WithNoClientsFound_Returns400() throws Exception {

        String name = "Несуществующий";
        String surname = "Клиент";

        when(clientService.getClientsByNameAndSurname(name, surname)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/client/search")
                .param("name", name)
                .param("surname", surname))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clientService, times(1)).getClientsByNameAndSurname(name, surname);
    }

    @Test
    void getClientsByNameAndSurname_WithBlankParameters_Returns400() throws Exception {

        mockMvc.perform(get("/api/v1/client/search")
                .param("name", "")
                .param("surname", " "))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).getClientsByNameAndSurname(anyString(), anyString());
    }

    @Test
    void getClientsByNameAndSurname_WithMissingParameters_Returns400() throws Exception {

        mockMvc.perform(get("/api/v1/client/search"))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).getClientsByNameAndSurname(anyString(), anyString());
    }

    @Test
    void getAllClients_WithValidPagination_Returns200() throws Exception {
        List<Client> clients = List.of(createClientEntity());
        int limit = 10;
        int offset = 0;

        when(clientService.getAllClients(limit, offset)).thenReturn(clients);

        mockMvc.perform(get("/api/v1/client/all")
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(clientService, times(1)).getAllClients(limit, offset);
    }

    @Test
    void getAllClients_WithNegativeLimit_Returns400() throws Exception {
        mockMvc.perform(get("/api/v1/client/all")
                .param("limit", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("getAllClients.limit: must be greater than 0")));

        verify(clientService, never()).getAllClients(anyInt(), anyInt());
    }

    @Test
    void getAllClients_WithNegativeOffset_Returns400() throws Exception {
        mockMvc.perform(get("/api/v1/client/all")
                .param("offset", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string(containsString("getAllClients.offset: must be greater than or equal to 0")));

        verify(clientService, never()).getAllClients(anyInt(), anyInt());
    }

    @Test
    void getAllClients_WithoutPagination_Returns200() throws Exception {

        List<Client> clients = List.of(createClientEntity());

        when(clientService.getAllClients(null, null)).thenReturn(clients);

        mockMvc.perform(get("/api/v1/client/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(clientService, times(1)).getAllClients(null, null);
    }

    @Test
    void updateClientAddress_WithExistingClientAndValidData_Returns200() throws Exception {

        AddressDTO addressDto = createValidAddressDTO();
        Client updatedClient = createClientEntity();

        doNothing().when(clientService).existsById(existingClientId);
        when(clientService.updateClientAddress(existingClientId, addressDto)).thenReturn(updatedClient);

        mockMvc.perform(patch("/api/v1/client/update-address/{id}", existingClientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingClientId.toString()));

        verify(clientService, times(1)).existsById(existingClientId);
        verify(clientService, times(1)).updateClientAddress(existingClientId, addressDto);
    }

    @Test
    void updateClientAddress_WithNonExistingClient_Returns404() throws Exception {
        AddressDTO addressDto = createValidAddressDTO();

        doThrow(NotFoundException.forClient(nonExistingClientId))
                .when(clientService).updateClientAddress(eq(nonExistingClientId), any(AddressDTO.class));

        mockMvc.perform(patch("/api/v1/client/update-address/{id}", nonExistingClientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Клиент с ID " + nonExistingClientId + " не найден")));

        verify(clientService, times(1)).updateClientAddress(eq(nonExistingClientId), any(AddressDTO.class));
    }

    @Test
    void updateClientAddress_WithInvalidAddressData_Returns400() throws Exception {

        AddressDTO invalidAddressDto = new AddressDTO(); // Все поля null

        mockMvc.perform(patch("/api/v1/client/update-address/{id}", existingClientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddressDto)))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).existsById(any(UUID.class));
        verify(clientService, never()).updateClientAddress(any(UUID.class), any(AddressDTO.class));
    }
}