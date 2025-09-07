package com.example.shop.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank(message = "Имя клиента обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Фамилия клиента обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Column(name = "surname", nullable = false)
    private String surname;

    @NotNull
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    // @Pattern(regexp = "^[MF]$", message = "Пол должен быть 'M' или 'F'")
    @Column(name = "gender")
    private String gender;

    // авто
    @Column(name = "registration_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime registrationDate;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }

    @NotNull(message = "Адрес обязателен")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", columnDefinition = "uuid")
    private Address address;
}