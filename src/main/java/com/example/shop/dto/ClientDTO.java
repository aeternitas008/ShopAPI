package com.example.shop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientDTO {

    private String name;

    private String surname;

    private LocalDate birthday;

    private String gender;

    private LocalDateTime registrationDate;

    private AddressDTO addressDto;
}