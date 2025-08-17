package com.example.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SupplierDTO {

    String name;

    String surname;

    AddressDTO addressDto;

    String phoneNumber;
}