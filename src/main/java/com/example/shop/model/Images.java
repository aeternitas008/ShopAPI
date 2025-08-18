package com.example.shop.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
public class Images {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Lob
    @Column(name = "image", length = 10485760) // 10MB
    private byte[] image;

}