package com.example.shop.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDTO {

    String title;

    String category;

    int price;

    private UUID supplierId;

    ImageDTO imageDto;

    int availableQuantity;

    LocalDate lastRestokeDate;

}