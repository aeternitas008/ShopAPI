package com.example.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop.dto.SupplierDTO;
import com.example.shop.model.Supplier;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface SupplierMapper {

    @Mapping(source = "addressDto", target = "address")
    @Mapping(target = "id", ignore = true)
    Supplier toEntity(SupplierDTO dto);

    @Mapping(source = "address", target = "addressDto")
    SupplierDTO toDto(Supplier entity);

}
