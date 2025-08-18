package com.example.shop.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.shop.dto.ImageDTO;
import com.example.shop.mapper.ImageMapper;
import com.example.shop.model.Images;
import com.example.shop.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    private final ProductService productService;

    // 0) Добавить/Создать картинку
    public Images addImage(ImageDTO imageDto) {
        return imageRepository.save(imageMapper.toEntity(imageDto));
    }

    // 1) Обновить картинку у продукта
    public Images updateImageProduct(UUID productId, ImageDTO imageDto) {
        Images image = addImage(imageDto);
        productService.updateImage(productId, image);
        return imageRepository.save(image);
    }

    // 2) Обновить картинку по id
    public Images updateImage(UUID id, ImageDTO imageDTO) {
        Images image = imageRepository.findById(id).orElseThrow();
        image.setImage(imageDTO.getImage());
        return imageRepository.save(image);
    }

    // 3) Удаление клиента
    public void deleteImageById(UUID id) {
        imageRepository.deleteById(id);
    }

    // 4) Найти картинку у продукта
    public Images findImageProduct(UUID productId) {
        return productService.getImage(productId);
    }

    // 5) Поиск по имени и фамилии
    public Images getById(UUID id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public boolean existsById(UUID id) {
        return imageRepository.existsById(id);
    }
}