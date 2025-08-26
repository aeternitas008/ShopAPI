package com.example.shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SupplierDTO {

    @NotBlank(message = "Name must not be empty")
    @Size(min = 2, max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Surname must not be empty")
    @Size(min = 2, max = 100, message = "Surname must be at most 100 characters")
    private String surname;

    @Valid
    private AddressDTO addressDto;

    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "^\\+?[0-9\\- ]{7,20}$", message = "Phone number must be valid")
    private String phoneNumber;
}