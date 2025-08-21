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
import com.example.shop.dto.SupplierDTO;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.exception.SupplierNotFoundException;
import com.example.shop.mapper.AddressMapper;
import com.example.shop.mapper.SupplierMapper;
import com.example.shop.model.Supplier;
import com.example.shop.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private SupplierService supplierService;

    private final UUID existingSupplierId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingSupplierId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    private SupplierDTO createValidSupplierDTO() {
        SupplierDTO dto = new SupplierDTO();
        dto.setName("Иван");
        dto.setSurname("Иванов");
        dto.setPhoneNumber("+79991234567");
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
    void addSupplier_WithValidData_ShouldSaveAndReturnSupplier() {
        // given
        SupplierDTO supplierDTO = createValidSupplierDTO();
        Supplier supplierEntity = createSupplierEntity();

        when(supplierMapper.toEntity(supplierDTO)).thenReturn(supplierEntity);
        when(supplierRepository.save(supplierEntity)).thenReturn(supplierEntity);

        // when
        Supplier result = supplierService.addSupplier(supplierDTO);

        // then
        assertNotNull(result);
        assertEquals(existingSupplierId, result.getId());
        assertEquals("Иван", result.getName());
        assertEquals("Иванов", result.getSurname());

        verify(supplierMapper, times(1)).toEntity(supplierDTO);
        verify(supplierRepository, times(1)).save(supplierEntity);
    }

    @Test
    void updateAddress_WithExistingSupplier_ShouldUpdateAddress() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();
        Supplier existingSupplier = createSupplierEntity();
        com.example.shop.model.Address addressEntity = new com.example.shop.model.Address();

        when(supplierRepository.findById(existingSupplierId)).thenReturn(Optional.of(existingSupplier));
        when(addressMapper.toEntity(addressDTO)).thenReturn(addressEntity);
        when(supplierRepository.save(existingSupplier)).thenReturn(existingSupplier);

        // when
        Supplier result = supplierService.updateAddress(existingSupplierId, addressDTO);

        // then
        assertNotNull(result);
        assertEquals(addressEntity, result.getAddress());

        verify(supplierRepository, times(1)).findById(existingSupplierId);
        verify(addressMapper, times(1)).toEntity(addressDTO);
        verify(supplierRepository, times(1)).save(existingSupplier);
    }

    @Test
    void updateAddress_WithNonExistingSupplier_ShouldThrowException() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();

        when(supplierRepository.findById(nonExistingSupplierId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(Exception.class, () -> {
            supplierService.updateAddress(nonExistingSupplierId, addressDTO);
        });

        verify(supplierRepository, times(1)).findById(nonExistingSupplierId);
        verify(addressMapper, never()).toEntity(any());
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteSupplier_WithExistingSupplier_ShouldDeleteSupplier() {
        // given
        when(supplierRepository.existsById(existingSupplierId)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(existingSupplierId);

        // when
        supplierService.deleteSupplier(existingSupplierId);

        // then
        verify(supplierRepository, times(1)).existsById(existingSupplierId);
        verify(supplierRepository, times(1)).deleteById(existingSupplierId);
    }

    @Test
    void deleteSupplier_WithNonExistingSupplier_ShouldThrowSupplierNotFoundException() {
        // given
        when(supplierRepository.existsById(nonExistingSupplierId)).thenReturn(false);

        // when & then
        assertThrows(SupplierNotFoundException.class, () -> {
            supplierService.deleteSupplier(nonExistingSupplierId);
        });

        verify(supplierRepository, times(1)).existsById(nonExistingSupplierId);
        verify(supplierRepository, never()).deleteById(any());
    }

    @Test
    void getAll_WithPagination_ShouldReturnPagedSuppliers() {
        // given
        int limit = 10;
        int offset = 0;
        Pageable pageable = PageRequest.of(offset, limit);
        List<Supplier> suppliers = List.of(createSupplierEntity());
        Page<Supplier> page = new PageImpl<>(suppliers);

        when(supplierRepository.findAll(pageable)).thenReturn(page);

        // when
        List<Supplier> result = supplierService.getAll(limit, offset);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(supplierRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAll_WithoutPagination_ShouldReturnAllSuppliers() {
        // given
        List<Supplier> suppliers = List.of(createSupplierEntity());

        when(supplierRepository.findAll()).thenReturn(suppliers);

        // when
        List<Supplier> result = supplierService.getAll(null, null);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(supplierRepository, times(1)).findAll();
        verify(supplierRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getById_WithExistingSupplier_ShouldReturnSupplier() {
        // given
        Supplier supplier = createSupplierEntity();

        when(supplierRepository.findById(existingSupplierId)).thenReturn(Optional.of(supplier));

        // when
        Supplier result = supplierService.getById(existingSupplierId);

        // then
        assertNotNull(result);
        assertEquals(existingSupplierId, result.getId());

        verify(supplierRepository, times(1)).findById(existingSupplierId);
    }

    @Test
    void getById_WithNonExistingSupplier_ShouldThrowException() {
        // given
        when(supplierRepository.findById(nonExistingSupplierId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(Exception.class, () -> {
            supplierService.getById(nonExistingSupplierId);
        });

        verify(supplierRepository, times(1)).findById(nonExistingSupplierId);
    }

    @Test
    void updateSupplierAddress_WithExistingSupplier_ShouldUpdateAddress() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();
        Supplier existingSupplier = createSupplierEntity();
        com.example.shop.model.Address addressEntity = new com.example.shop.model.Address();

        when(supplierRepository.findById(existingSupplierId)).thenReturn(Optional.of(existingSupplier));
        when(addressMapper.toEntity(addressDTO)).thenReturn(addressEntity);
        when(supplierRepository.save(existingSupplier)).thenReturn(existingSupplier);

        // when
        Supplier result = supplierService.updateSupplierAddress(existingSupplierId, addressDTO);

        // then
        assertNotNull(result);
        assertEquals(addressEntity, result.getAddress());

        verify(supplierRepository, times(1)).findById(existingSupplierId);
        verify(addressMapper, times(1)).toEntity(addressDTO);
        verify(supplierRepository, times(1)).save(existingSupplier);
    }

    @Test
    void getSupplierById_WithExistingSupplier_ShouldReturnSupplier() {
        // given
        Supplier supplier = createSupplierEntity();

        when(supplierRepository.findById(existingSupplierId)).thenReturn(Optional.of(supplier));

        // when
        Supplier result = supplierService.getSupplierById(existingSupplierId);

        // then
        assertNotNull(result);
        assertEquals(existingSupplierId, result.getId());

        verify(supplierRepository, times(1)).findById(existingSupplierId);
    }

    @Test
    void getSupplierById_WithNonExistingSupplier_ShouldThrowNotFoundException() {
        // given
        when(supplierRepository.findById(nonExistingSupplierId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            supplierService.getSupplierById(nonExistingSupplierId);
        });

        verify(supplierRepository, times(1)).findById(nonExistingSupplierId);
    }

    @Test
    void existsById_WithExistingSupplier_ShouldNotThrowException() {
        // given
        when(supplierRepository.existsById(existingSupplierId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> {
            supplierService.existsById(existingSupplierId);
        });

        verify(supplierRepository, times(1)).existsById(existingSupplierId);
    }

    @Test
    void existsById_WithNonExistingSupplier_ShouldThrowNotFoundException() {
        // given
        when(supplierRepository.existsById(nonExistingSupplierId)).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            supplierService.existsById(nonExistingSupplierId);
        });

        verify(supplierRepository, times(1)).existsById(nonExistingSupplierId);
    }

    @Test
    void getAllSuppliersValidated_WithValidPagination_ShouldReturnSuppliers() {
        // given
        int limit = 10;
        int offset = 0;
        List<Supplier> suppliers = List.of(createSupplierEntity());

        when(supplierService.getAll(limit, offset)).thenReturn(suppliers);

        // when
        List<Supplier> result = supplierService.getAllSuppliersValidated(limit, offset);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(supplierRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllSuppliersValidated_WithInvalidLimit_ShouldThrowBadRequestException() {
        // given
        int limit = -1;
        int offset = 0;

        // when & then
        assertThrows(BadRequestException.class, () -> {
            supplierService.getAllSuppliersValidated(limit, offset);
        });

        verify(supplierRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllSuppliersValidated_WithInvalidOffset_ShouldThrowBadRequestException() {
        // given
        int limit = 10;
        int offset = -1;

        // when & then
        assertThrows(BadRequestException.class, () -> {
            supplierService.getAllSuppliersValidated(limit, offset);
        });

        verify(supplierRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllSuppliersValidated_WithEmptyResult_ShouldThrowNotFoundException() {
        // given
        int limit = 10;
        int offset = 0;

        when(supplierService.getAll(limit, offset)).thenReturn(List.of());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            supplierService.getAllSuppliersValidated(limit, offset);
        });

        verify(supplierRepository, times(1)).findAll(any(Pageable.class));
    }
}