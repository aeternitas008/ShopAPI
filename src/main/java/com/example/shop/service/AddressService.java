package com.example.shop.service;

import org.springframework.stereotype.Service;

import com.example.shop.dto.AddressDTO;
import com.example.shop.model.Address;
import com.example.shop.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public Address addAddress(AddressDTO addressDTO) {
        Address address = new Address();
        address.setCountry(addressDTO.getCountry());
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getHouse());
        address.setHouse(addressDTO.getHouse());
        return addressRepository.save(address);
    }
}
