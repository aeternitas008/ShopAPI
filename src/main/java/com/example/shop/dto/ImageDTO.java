package com.example.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageDTO {

    @NotNull(message = "image must not be empty")
    byte[] image;
}