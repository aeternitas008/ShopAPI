package com.example.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressDTO {

    @NotBlank(message = "Country must not be empty")
    @Size(min = 1, max = 100, message = "Country must be at most 100 characters")
    private String country;

    @NotBlank(message = "City must not be empty")
    @Size(min = 1, max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotBlank(message = "Street must not be empty")
    @Size(min = 1, max = 200, message = "Street must be at most 200 characters")
    private String street;

    @NotBlank(message = "House must not be empty")
    @Size(min = 1, max = 20, message = "House must be at most 20 characters")
    private String house;
}