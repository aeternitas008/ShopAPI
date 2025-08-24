package com.example.shop.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.example.shop.dto.SupplierDTO;
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Supplier;
import com.example.shop.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SupplierController.class)
@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID existingSupplierId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingSupplierId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    // Вспомогательные методы для создания тестовых данных
    private SupplierDTO createValidSupplierDTO() {
        SupplierDTO dto = new SupplierDTO();
        dto.setName("Иван");
        dto.setSurname("Иванов");
        dto.setPhoneNumber("+79991234567");
        dto.setAddressDto(createValidAddressDTO());
        return dto;
    }

    private Supplier createSupplierEntity() {
        Supplier supplier = new Supplier();
        supplier.setId(existingSupplierId);
        supplier.setName("Иван");
        supplier.setSurname("Иванов");
        supplier.setPhoneNumber("+79991234567");
        return supplier;
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
    void addSupplier_WithValidData_Returns200() throws Exception {
        // given
        SupplierDTO validDto = createValidSupplierDTO();
        Supplier createdSupplier = createSupplierEntity();

        when(supplierService.addSupplier(any(SupplierDTO.class))).thenReturn(createdSupplier);

        // when & then
        mockMvc.perform(post("/api/v1/supplier/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingSupplierId.toString()))
                .andExpect(jsonPath("$.name").value("Test Supplier"));

        verify(supplierService, times(1)).addSupplier(any(SupplierDTO.class));
    }

    @Test
    void updateSupplierAddress_WithExistingSupplier_Returns200() throws Exception {
        // given
        AddressDTO addressDto = createValidAddressDTO();
        Supplier updatedSupplier = createSupplierEntity();

        doNothing().when(supplierService).existsById(existingSupplierId);
        when(supplierService.updateSupplierAddress(existingSupplierId, addressDto)).thenReturn(updatedSupplier);

        // when & then
        mockMvc.perform(patch("/api/v1/supplier/updateAddress/{id}", existingSupplierId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk());

        verify(supplierService, times(1)).existsById(existingSupplierId);
        verify(supplierService, times(1)).updateSupplierAddress(existingSupplierId, addressDto);
    }

    @Test
    void updateSupplierAddress_WithNonExistingSupplier_Returns404() throws Exception {
        // given
        AddressDTO addressDto = createValidAddressDTO();

        doThrow(NotFoundException.forSupplier(nonExistingSupplierId))
                .when(supplierService).existsById(nonExistingSupplierId);

        // when & then
        mockMvc.perform(patch("/api/v1/supplier/updateAddress/{id}", nonExistingSupplierId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("не найден")));

        verify(supplierService, times(1)).existsById(nonExistingSupplierId);
        verify(supplierService, never()).updateSupplierAddress(any(UUID.class), any(AddressDTO.class));
    }

    @Test
    void deleteSupplier_WithExistingSupplier_Returns200() throws Exception {
        // given
        doNothing().when(supplierService).existsById(existingSupplierId);
        doNothing().when(supplierService).deleteSupplier(existingSupplierId);

        // when & then
        mockMvc.perform(delete("/api/v1/supplier/{id}", existingSupplierId))
                .andExpect(status().isOk());

        verify(supplierService, times(1)).existsById(existingSupplierId);
        verify(supplierService, times(1)).deleteSupplier(existingSupplierId);
    }

    @Test
    void deleteSupplier_WithNonExistingSupplier_Returns404() throws Exception {
        // given
        doThrow(NotFoundException.forSupplier(nonExistingSupplierId))
                .when(supplierService).existsById(nonExistingSupplierId);

        // when & then
        mockMvc.perform(delete("/api/v1/supplier/{id}", nonExistingSupplierId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("не найден")));

        verify(supplierService, times(1)).existsById(nonExistingSupplierId);
        verify(supplierService, never()).deleteSupplier(any(UUID.class));
    }

    @Test
    void getAllSuppliers_WithValidPagination_Returns200() throws Exception {
        // given
        List<Supplier> suppliers = List.of(createSupplierEntity());
        int limit = 10;
        int offset = 0;

        when(supplierService.getAll(limit, offset)).thenReturn(suppliers);

        // when & then
        mockMvc.perform(get("/api/v1/supplier/all")
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(supplierService, times(1)).getAll(limit, offset);
    }

    @Test
    void getAllSuppliers_WithoutPagination_Returns200() throws Exception {
        // given
        List<Supplier> suppliers = List.of(createSupplierEntity());

        when(supplierService.getAll(null, null)).thenReturn(suppliers);

        // when & then
        mockMvc.perform(get("/api/v1/supplier/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(supplierService, times(1)).getAll(null, null);
    }

    @Test
    void getAllSuppliers_WithInvalidLimit_Returns400() throws Exception {
        // given
        when(supplierService.getAll(-1, 0))
                .thenThrow(new IllegalArgumentException("Limit должен быть > 0"));

        // when & then
        mockMvc.perform(get("/api/v1/supplier/all")
                .param("limit", "-1")
                .param("offset", "0"))
                .andExpect(status().isBadRequest());

        verify(supplierService, times(1)).getAll(-1, 0);
    }

    @Test
    void getSupplier_WithExistingSupplier_Returns200() throws Exception {
        Supplier supplier = createSupplierEntity();

        when(supplierService.getSupplierById(existingSupplierId)).thenReturn(supplier);

        mockMvc.perform(get("/api/v1/supplier/{id}", existingSupplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingSupplierId.toString()));

        verify(supplierService, times(1)).getSupplierById(existingSupplierId);
    }

    @Test
    void getSupplier_WithNonExistingSupplier_Returns404() throws Exception {
        // given
        when(supplierService.getSupplierById(nonExistingSupplierId))
                .thenThrow(NotFoundException.forSupplier(nonExistingSupplierId));

        // when & then
        mockMvc.perform(get("/api/v1/supplier/{id}", nonExistingSupplierId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("не найден")));

        verify(supplierService, times(1)).getSupplierById(nonExistingSupplierId);
    }
}