package com.example.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop.dto.ImageDTO;
import com.example.shop.model.Images;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "id", ignore = true)
    Images toEntity(ImageDTO dto);

    @Mapping(source = "image", target = "image")
    ImageDTO toDto(Images entity);
}
