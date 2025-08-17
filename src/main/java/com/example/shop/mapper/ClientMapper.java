package com.example.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop.dto.ClientDTO;
import com.example.shop.model.Client;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "addressDto", target = "address")
    Client toEntity(ClientDTO dto);

    @Mapping(source = "address", target = "addressDto")
    ClientDTO toDto(Client entity);

}
