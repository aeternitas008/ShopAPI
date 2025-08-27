package com.example.shop.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shop.dto.ImageDTO;
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Images;
import com.example.shop.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ImageController.class)
@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID existingImageId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingImageId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private final UUID existingProductId = UUID.fromString("b2c3d4e5-f6b7-8901-bcde-f23456789012");

    // Вспомогательные методы для создания тестовых данных
    private ImageDTO createValidImageDTO() {
        ImageDTO dto = ImageDTO.builder()
                .image("test-image-data".getBytes())
                .build();
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
    void addImage_WithValidData_Returns200() throws Exception {
        // given
        ImageDTO imageDto = createValidImageDTO();
        Images createdImage = createImageEntity();

        when(imageService.updateImageProduct(any(UUID.class), any(ImageDTO.class))).thenReturn(createdImage);

        // when & then
        mockMvc.perform(post("/api/v1/image/add")
                .param("productId", existingProductId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imageDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(existingImageId.toString()));

        verify(imageService, times(1)).updateImageProduct(any(UUID.class), any(ImageDTO.class));
    }

    @Test
    void addImage_WithServiceException_Returns400() throws Exception {
        // given
        ImageDTO imageDto = createValidImageDTO();
        when(imageService.updateImageProduct(existingProductId, imageDto))
                .thenThrow(NotFoundException.forProduct(existingProductId));

        // when & then
        mockMvc.perform(post("/api/v1/image/add")
                .param("productId", existingProductId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imageDto)))
                .andExpect(status().isBadRequest());

        verify(imageService, times(1)).updateImageProduct(existingProductId, imageDto);
    }

    @Test
    void addImage_WithInvalidProductId_Returns400() throws Exception {
        // given
        ImageDTO imageDto = createValidImageDTO();

        // when & then
        mockMvc.perform(post("/api/v1/image/add")
                .param("productId", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imageDto)))
                .andExpect(status().isBadRequest());

        verify(imageService, never()).updateImageProduct(any(UUID.class), any(ImageDTO.class));
    }

    @Test
    void updateImage_WithExistingId_Returns200() throws Exception {
        // given
        ImageDTO imageDto = createValidImageDTO();
        Images updatedImage = createImageEntity();

        doNothing().when(imageService).existsById(nonExistingImageId);
        when(imageService.updateImage(existingImageId, imageDto)).thenReturn(updatedImage);

        // when & then
        mockMvc.perform(patch("/api/v1/image/{id}", existingImageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imageDto)))
                .andExpect(status().isOk());

        verify(imageService, times(1)).existsById(existingImageId);
        verify(imageService, times(1)).updateImage(existingImageId, imageDto);
    }

    @Test
    void updateImage_WithNonExistingId_Returns400() throws Exception {
        // given
        ImageDTO imageDto = createValidImageDTO();
        doNothing().when(imageService).existsById(nonExistingImageId);

        // when & then
        mockMvc.perform(patch("/api/v1/image/{id}", nonExistingImageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imageDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("не найдено")));

        verify(imageService, times(1)).existsById(nonExistingImageId);
        verify(imageService, never()).updateImage(any(UUID.class), any(ImageDTO.class));
    }

    @Test
    void deleteImage_WithExistingId_Returns200() throws Exception {
        doNothing().when(imageService).existsById(nonExistingImageId);
        doNothing().when(imageService).deleteImageById(existingImageId);

        mockMvc.perform(delete("/api/v1/image/{id}", existingImageId))
                .andExpect(status().isOk());

        verify(imageService, times(1)).existsById(existingImageId);
        verify(imageService, times(1)).deleteImageById(existingImageId);
    }

    @Test
    void deleteImage_WithNonExistingId_Returns400() throws Exception {
        doNothing().when(imageService).existsById(nonExistingImageId);

        mockMvc.perform(delete("/api/v1/image/{id}", nonExistingImageId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("не найдено")));

        verify(imageService, times(1)).existsById(nonExistingImageId);
        verify(imageService, never()).deleteImageById(any(UUID.class));
    }

    @Test
    void getImagesByProduct_WithExistingProduct_Returns200() throws Exception {
        // given
        Images image = createImageEntity();

        when(imageService.findImageProduct(existingProductId)).thenReturn(image);

        // when & then
        mockMvc.perform(get("/api/v1/image/by-product/{productId}", existingProductId))
                .andExpect(status().isOk());

        verify(imageService, times(1)).findImageProduct(existingProductId);
    }

    @Test
    void getImagesByProduct_WithNonExistingProduct_Returns404() throws Exception {
        // given
        when(imageService.findImageProduct(nonExistingImageId)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/v1/image/by-product/{productId}", nonExistingImageId))
                .andExpect(status().isNotFound());

        verify(imageService, times(1)).findImageProduct(nonExistingImageId);
    }

    @Test
    void getImageById_WithExistingImage_Returns200() throws Exception {
        // given
        Images image = createImageEntity();

        when(imageService.getImageById(existingImageId)).thenReturn(image);

        // when & then
        mockMvc.perform(get("/api/v1/image/{id}", existingImageId))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(image.getImage()));

        verify(imageService, times(1)).getImageById(existingImageId);
    }

    @Test
    void getImageById_WithNonExistingImage_Returns404() throws Exception {
        // given
        when(imageService.getImageById(nonExistingImageId)).thenThrow(NotFoundException.forImage(nonExistingImageId));

        // when & then
        mockMvc.perform(get("/api/v1/image/{id}", nonExistingImageId))
                .andExpect(status().isNotFound());

        verify(imageService, times(1)).getImageById(nonExistingImageId);
    }

    @Test
    void getImageById_WithImageNullData_Returns404() throws Exception {
        // given
        Images image = createImageEntityWithNullData();

        when(imageService.getImageById(existingImageId)).thenReturn(image);

        // when & then
        mockMvc.perform(get("/api/v1/image/{id}", existingImageId))
                .andExpect(status().isNotFound());

        verify(imageService, times(1)).getImageById(existingImageId);
    }
}
