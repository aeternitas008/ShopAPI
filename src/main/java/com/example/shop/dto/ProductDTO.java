package com.example.shop.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDTO {

    String title;

    String category;

    int price;

    SupplierDTO supplierDto;

    ImageDTO imageDto;

    int availableQuantity;

    LocalDate lastRestokeDate;

}