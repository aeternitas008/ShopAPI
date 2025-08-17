package com.example.shop.model;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Table(name = "supplier")
@Entity
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Column(name = "name", nullable = false)
    String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Column(name = "surname", nullable = false)
    String surname;

    @NotNull(message = "Адрес обязателен")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", columnDefinition = "uuid")
    private Address address;

    @NotBlank(message = "Номер телефона обязателен")
    @Schema(type = "string", pattern = "\\d{11}", example = "79211234567")
    @Size(min = 11, max = 11, message = "Номер должен быть указан в формате 11 цифр")
    @Column(name = "phone_number", nullable = false)
    String phoneNumber;
}