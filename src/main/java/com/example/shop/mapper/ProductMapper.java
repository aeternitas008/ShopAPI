package com.example.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop.dto.ProductDTO;
import com.example.shop.model.Product;

@Mapper(componentModel = "spring", uses = { ImageMapper.class, SupplierMapper.class })
public interface ProductMapper {

    @Mapping(target = "supplier", ignore = true)
    @Mapping(source = "imageDto", target = "image")
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "image", target = "imageDto")
    ProductDTO toDto(Product entity);

}
