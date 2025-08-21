package com.example.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.shop.dto.AddressDTO;
import com.example.shop.model.Address;
import com.example.shop.repository.AddressRepository;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private AddressDTO createValidAddressDTO() {
        AddressDTO dto = new AddressDTO();
        dto.setCountry("Россия");
        dto.setCity("Москва");
        dto.setStreet("Тверская");
        dto.setHouse("1");
        return dto;
    }

    private Address createAddressEntity() {
        Address address = new Address();
        address.setCountry("Россия");
        address.setCity("Москва");
        address.setStreet("Тверская");
        address.setHouse("1");
        return address;
    }

    @Test
    void addAddress_WithValidData_ShouldSaveAndReturnAddress() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();
        Address expectedAddress = createAddressEntity();

        when(addressRepository.save(any(Address.class))).thenReturn(expectedAddress);

        // when
        Address result = addressService.addAddress(addressDTO);

        // then
        assertNotNull(result);
        assertEquals("Россия", result.getCountry());
        assertEquals("Москва", result.getCity());
        assertEquals("Тверская", result.getStreet());
        assertEquals("1", result.getHouse());

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void addAddress_WithNullFields_ShouldSaveAddressWithNullValues() {
        // given
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry(null);
        addressDTO.setCity("Москва");
        addressDTO.setStreet(null);
        addressDTO.setHouse("1");

        Address savedAddress = new Address();
        savedAddress.setCountry(null);
        savedAddress.setCity("Москва");
        savedAddress.setStreet(null);
        savedAddress.setHouse("1");

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        // when
        Address result = addressService.addAddress(addressDTO);

        // then
        assertNotNull(result);
        assertNull(result.getCountry());
        assertEquals("Москва", result.getCity());
        assertNull(result.getStreet());
        assertEquals("1", result.getHouse());

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void addAddress_ShouldCallRepositorySaveExactlyOnce() {
        // given
        AddressDTO addressDTO = createValidAddressDTO();
        Address savedAddress = createAddressEntity();

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        // when
        addressService.addAddress(addressDTO);

        // then
        verify(addressRepository, times(1)).save(any(Address.class));
        verifyNoMoreInteractions(addressRepository);
    }
}