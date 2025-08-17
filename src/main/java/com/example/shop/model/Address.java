package com.example.shop.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank(message = "Страна обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank(message = "Город обязателен")
    @Size(min = 2, max = 50, message = "Город должен быть от 2 до 50 символов")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "Улица обязательна")
    @Size(min = 2, max = 50, message = "Улица должна быть от 2 до 50 символов")
    @Column(name = "street", nullable = false)
    private String street;

    @Size(min = 1, max = 10, message = "Номер дома от 1 до 10 символов")
    @Column(name = "house")
    private String house;
}