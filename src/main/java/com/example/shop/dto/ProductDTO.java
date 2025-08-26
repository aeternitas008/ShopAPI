package com.example.shop.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDTO {

    @NotBlank(message = "Title must not be empty")
    @Size(min = 2, max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Category must not be empty")
    @Size(min = 2, max = 100, message = "Category must be at most 100 characters")
    private String category;

    @Min(value = 0, message = "Price must be positive or zero")
    private int price;

    @NotNull(message = "Supplier ID must not be null")
    private UUID supplierId;

    @Valid
    private ImageDTO imageDto;

    @Min(value = 0, message = "Available quantity must be positive or zero")
    private int availableQuantity;

    private LocalDate lastRestokeDate;

}