package com.example.shop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientDTO {

    @NotBlank(message = "Name must not be empty")
    @Size(min = 2, max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Surname must not be empty")
    @Size(min = 2, max = 100, message = "Surname must be at most 100 characters")
    private String surname;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @NotBlank(message = "Gender must not be empty")
    @Size(min = 1, message = "Only M/F are allowed")
    private String gender;

    @NotNull(message = "Registration date is required")
    private LocalDateTime registrationDate;

    private AddressDTO addressDto;
}