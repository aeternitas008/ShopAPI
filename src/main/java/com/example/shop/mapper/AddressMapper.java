package com.example.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop.dto.AddressDTO;
import com.example.shop.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressDTO dto);

    AddressDTO toDto(Address entity);
}
