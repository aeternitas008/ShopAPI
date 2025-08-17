package com.example.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop.dto.ProductDTO;
import com.example.shop.model.Product;

@Mapper(componentModel = "spring", uses = { ImageMapper.class, SupplierMapper.class })
public interface ProductMapper {

    @Mapping(source = "supplierDto", target = "supplier")
    @Mapping(source = "imageDto", target = "image")
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(source = "supplier", target = "supplierDto")
    @Mapping(source = "image", target = "imageDto")
    ProductDTO toDto(Product entity);

}
