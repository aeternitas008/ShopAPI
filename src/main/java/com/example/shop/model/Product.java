package com.example.shop.model;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 2, max = 50, message = "Длина не меньше 2 и не больше 50")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Категория не может быть пустой")
    @Size(min = 2, max = 50, message = "Длина не меньше 2 и не больше 50")
    @Column(name = "category", nullable = false)
    private String category;

    @Positive(message = "Стоимость должна быть положительной")
    @Column(name = "price", nullable = false)
    private int price;

    @NotNull(message = "Поставщик обязателен")
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @NotNull(message = "Изображение обязательно")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", nullable = false, columnDefinition = "uuid")
    private Image image;

    @PositiveOrZero(message = "Количество должно быть неотрицательным")
    @Column(name = "available_quantity", nullable = false)
    private long availableQuantity = 0;

    @Column(name = "last_restoke_date")
    private LocalDate lastRestokeDate = LocalDate.now();
}