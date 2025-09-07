package com.example.shop.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Images;
import com.example.shop.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ProductService productService;

    // 0) Добавить/Создать картинку
    public Images addImage(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            throw new IllegalArgumentException("Некорректное/пустое изображение");
        }
        Images image = new Images();
        image.setImage(byteArray);
        return imageRepository.save(image);
    }

    // 1) Добавить картинку продукту
    public Images addImageProduct(UUID productId, byte[] byteArray) {

        Images image = addImage(byteArray);
        productService.updateImage(productId, image);
        return imageRepository.save(image);
    }

    // 2) Обновить картинку по id
    public Images updateImage(UUID id, byte[] byteArray) {
        Images image = findById(id);
        image.setImage(byteArray);
        return imageRepository.save(image);
    }

    // 3) Удаление клиента
    public void deleteImageById(UUID id) {
        existsById(id);
        imageRepository.deleteById(id);
    }

    // 4) Найти картинку у продукта
    public Images findImageProduct(UUID productId) {
        return productService.getImage(productId);
    }

    public Images getImageById(UUID id) {
        Images image = findById(id);

        if (image.getImage() == null) {
            throw new BadRequestException("Изображение с ID " + id + " не содержит данных");
        }

        return image;
    }

    public void existsById(UUID id) {
        if (!imageRepository.existsById(id))
            throw NotFoundException.forImage(id);
    }

    public Images findById(UUID id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> NotFoundException.forImage(id));
    }

    public void validateImageHasData(UUID id) {
        Images image = findById(id);

        if (image.getImage() == null) {
            throw new BadRequestException("Изображение с ID " + id + " не содержит данных");
        }

    }
}