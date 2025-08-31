package com.example.shop.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    // Вспомогательные методы для создания тестовых данных
    private ImageDTO createInvalidImageDTO() {
        ImageDTO dto = ImageDTO.builder()
                .image(null)
                .build();
        return dto;
    }

    private byte[] createValidByteArray() {
        return "test-image-data".getBytes();
    }

    private Images createImageEntity() {
        Images image = new Images();
        image.setId(existingImageId);
        image.setImage(createValidByteArray());
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
        Images createdImage = createImageEntity();
        when(imageService.addImageProduct(any(UUID.class), any())).thenReturn(createdImage);

        mockMvc.perform(post("/api/v1/image/{productId}/add", existingProductId)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .content(createValidByteArray()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(existingImageId.toString()));

        verify(imageService, times(1)).addImageProduct(any(UUID.class), any());
    }

    @Test
    void addImage_WithInvalidProductId_Returns400() throws Exception {
        byte[] byteArray = createValidByteArray();
        mockMvc.perform(post("/api/v1/image/{productId/add", "invalid-uuid")
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .content(byteArray))
                .andExpect(status().isBadRequest());

        verify(imageService, never()).addImageProduct(any(UUID.class), any());
    }

    // @Test
    // void addImage_WithNullBody_Returns400() throws Exception {
    // mockMvc.perform(post("/api/v1/image/{productId}/add", existingProductId)
    // .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
    // .andExpect(status().isBadRequest());

    // verify(imageService, never()).addImageProduct(any(UUID.class), any());
    // }

    @Test
    void updateImage_WithExistingId_Returns200() throws Exception {
        Images updatedImage = createImageEntity();
        byte[] byteArray = createValidByteArray();
        when(imageService.updateImage(existingImageId, byteArray)).thenReturn(updatedImage);

        mockMvc.perform(patch("/api/v1/image/{id}", existingImageId)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .content(byteArray))
                .andExpect(status().isOk());

        verify(imageService, times(1)).updateImage(existingImageId, byteArray);
    }

    @Test
    void updateImage_WithNonExistingId_Returns404() throws Exception {
        byte[] byteArray = createValidByteArray();

        when(imageService.updateImage(nonExistingImageId, byteArray))
                .thenThrow(NotFoundException.forImage(nonExistingImageId));

        mockMvc.perform(patch("/api/v1/image/{id}", nonExistingImageId)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .content(byteArray))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("не найдено")));

        verify(imageService, times(1)).updateImage(nonExistingImageId, byteArray);
    }

    // почему должен бросать ошибку?
    @Test
    void deleteImage_WithExistingId_Returns200() throws Exception {
        doNothing().when(imageService).existsById(existingImageId);

        mockMvc.perform(delete("/api/v1/image/{id}", existingImageId))
                .andExpect(status().isOk());

        verify(imageService, times(1)).existsById(existingImageId);
        verify(imageService, times(1)).deleteImageById(existingImageId);
    }

    @Test
    void deleteImage_WithNonExistingId_Returns400() throws Exception {
        doThrow(NotFoundException.forImage(nonExistingImageId)).when(imageService).existsById(nonExistingImageId);

        mockMvc.perform(delete("/api/v1/image/{id}", nonExistingImageId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Изображение с ID " + nonExistingImageId + " не найдено")));

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
        when(imageService.findImageProduct(nonExistingImageId)).thenReturn(null);

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
}
