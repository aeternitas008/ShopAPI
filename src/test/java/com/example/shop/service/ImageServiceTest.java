package com.example.shop.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.shop.dto.ImageDTO;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.ImageMapper;
import com.example.shop.model.Images;
import com.example.shop.model.Product;
import com.example.shop.repository.ImageRepository;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ImageService imageService;

    private final UUID existingImageId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingImageId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private final UUID productId = UUID.fromString("b2c3d4e5-f6b7-8901-bcde-f23456789012");

    private ImageDTO createValidImageDTO() {
        ImageDTO dto = new ImageDTO();
        dto.setImage("test-image-data".getBytes());
        return dto;
    }

    private Images createImageEntity() {
        Images image = new Images();
        image.setId(existingImageId);
        image.setImage("test-image-data".getBytes());
        return image;
    }

    private Images createImageEntityWithNullData() {
        Images image = new Images();
        image.setId(existingImageId);
        image.setImage(null);
        return image;
    }

    @Test
    void addImage_WithValidData_ShouldSaveAndReturnImage() {
        // given
        ImageDTO imageDTO = createValidImageDTO();
        Images imageEntity = createImageEntity();

        when(imageMapper.toEntity(imageDTO)).thenReturn(imageEntity);
        when(imageRepository.save(imageEntity)).thenReturn(imageEntity);

        // when
        Images result = imageService.addImage(imageDTO);

        // then
        assertNotNull(result);
        assertEquals(existingImageId, result.getId());
        assertArrayEquals("test-image-data".getBytes(), result.getImage());

        verify(imageMapper, times(1)).toEntity(imageDTO);
        verify(imageRepository, times(1)).save(imageEntity);
    }

    @Test
    void updateImageProduct_WithValidData_ShouldSaveImageAndUpdateProduct() {
        // given
        ImageDTO imageDTO = createValidImageDTO();
        Images imageEntity = createImageEntity();

        when(imageMapper.toEntity(imageDTO)).thenReturn(imageEntity);
        when(imageRepository.save(imageEntity)).thenReturn(imageEntity);
        when(productService.updateImage(productId, imageEntity)).thenReturn(any(Product.class));

        // when
        Images result = imageService.updateImageProduct(productId, imageDTO);

        // then
        assertNotNull(result);
        assertEquals(existingImageId, result.getId());

        verify(imageMapper, times(1)).toEntity(imageDTO);
        verify(imageRepository, times(1)).save(imageEntity);
        verify(productService, times(1)).updateImage(productId, imageEntity);
    }

    @Test
    void updateImage_WithExistingImage_ShouldUpdateImageData() {
        // given
        ImageDTO imageDTO = createValidImageDTO();
        Images existingImage = createImageEntity();
        byte[] newImageData = "new-image-data".getBytes();
        imageDTO.setImage(newImageData);

        when(imageRepository.findById(existingImageId)).thenReturn(Optional.of(existingImage));
        when(imageRepository.save(existingImage)).thenReturn(existingImage);

        // when
        Images result = imageService.updateImage(existingImageId, imageDTO);

        // then
        assertNotNull(result);
        assertArrayEquals(newImageData, result.getImage());

        verify(imageRepository, times(1)).findById(existingImageId);
        verify(imageRepository, times(1)).save(existingImage);
    }

    @Test
    void updateImage_WithNonExistingImage_ShouldThrowNotFoundException() {
        // given
        ImageDTO imageDTO = createValidImageDTO();

        when(imageRepository.findById(nonExistingImageId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            imageService.updateImage(nonExistingImageId, imageDTO);
        });

        verify(imageRepository, times(1)).findById(nonExistingImageId);
        verify(imageRepository, never()).save(any());
    }

    @Test
    void deleteImageById_WithExistingImage_ShouldCallRepositoryDelete() {
        // given
        doNothing().when(imageRepository).deleteById(existingImageId);

        // when
        imageService.deleteImageById(existingImageId);

        // then
        verify(imageRepository, times(1)).deleteById(existingImageId);
    }

    @Test
    void findImageProduct_ShouldReturnProductImage() {
        // given
        Images productImage = createImageEntity();

        when(productService.getImage(productId)).thenReturn(productImage);

        // when
        Images result = imageService.findImageProduct(productId);

        // then
        assertNotNull(result);
        assertEquals(existingImageId, result.getId());

        verify(productService, times(1)).getImage(productId);
    }

    @Test
    void getImageById_WithExistingImageWithData_ShouldReturnImage() {
        // given
        Images image = createImageEntity();

        when(imageRepository.findById(existingImageId)).thenReturn(Optional.of(image));

        // when
        Images result = imageService.getImageById(existingImageId);

        // then
        assertNotNull(result);
        assertEquals(existingImageId, result.getId());
        assertArrayEquals("test-image-data".getBytes(), result.getImage());

        verify(imageRepository, times(1)).findById(existingImageId);
    }

    @Test
    void getImageById_WithNonExistingImage_ShouldThrowNotFoundException() {
        // given
        when(imageRepository.findById(nonExistingImageId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            imageService.getImageById(nonExistingImageId);
        });

        verify(imageRepository, times(1)).findById(nonExistingImageId);
    }

    @Test
    void getImageById_WithImageWithoutData_ShouldThrowBadRequestException() {
        // given
        Images image = createImageEntityWithNullData();

        when(imageRepository.findById(existingImageId)).thenReturn(Optional.of(image));

        // when & then
        assertThrows(BadRequestException.class, () -> {
            imageService.getImageById(existingImageId);
        });

        verify(imageRepository, times(1)).findById(existingImageId);
    }

    @Test
    void existsById_WithExistingImage_ShouldNotThrowException() {
        // given
        when(imageRepository.existsById(existingImageId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> {
            imageService.existsById(existingImageId);
        });

        verify(imageRepository, times(1)).existsById(existingImageId);
    }

    @Test
    void existsById_WithNonExistingImage_ShouldThrowNotFoundException() {
        // given
        when(imageRepository.existsById(nonExistingImageId)).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            imageService.existsById(nonExistingImageId);
        });

        verify(imageRepository, times(1)).existsById(nonExistingImageId);
    }

    @Test
    void validateImageHasData_WithImageWithData_ShouldNotThrowException() {
        // given
        Images image = createImageEntity();

        when(imageRepository.findById(existingImageId)).thenReturn(Optional.of(image));

        // when & then
        assertDoesNotThrow(() -> {
            imageService.validateImageHasData(existingImageId);
        });

        verify(imageRepository, times(1)).findById(existingImageId);
    }

    @Test
    void validateImageHasData_WithImageWithoutData_ShouldThrowBadRequestException() {
        // given
        Images image = createImageEntityWithNullData();

        when(imageRepository.findById(existingImageId)).thenReturn(Optional.of(image));

        // when & then
        assertThrows(BadRequestException.class, () -> {
            imageService.validateImageHasData(existingImageId);
        });

        verify(imageRepository, times(1)).findById(existingImageId);
    }

    @Test
    void validateImageHasData_WithNonExistingImage_ShouldThrowNotFoundException() {
        // given
        when(imageRepository.findById(nonExistingImageId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            imageService.validateImageHasData(nonExistingImageId);
        });

        verify(imageRepository, times(1)).findById(nonExistingImageId);
    }
}