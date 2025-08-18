package com.example.shop.controller;

import java.util.UUID;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dto.ImageDTO;
import com.example.shop.model.Images;
import com.example.shop.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 1) Добавление изображения
    @PostMapping("/add")
    @Operation(summary = "Добавление изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    public ResponseEntity<?> addImage(
            @RequestParam UUID productId,
            @RequestBody ImageDTO imageDto) {
        try {

            Images image = imageService.updateImageProduct(productId, imageDto);

            return ResponseEntity.ok(image.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2) Изменение изображения
    @PatchMapping("/{id}")
    @Operation(summary = "Обновление изображения")
    public ResponseEntity<?> updateImage(@PathVariable UUID id, @RequestBody ImageDTO imageDto) {
        if (!imageService.existsById(id)) {
            return ResponseEntity.badRequest().body("Изображение с ID " + id + " не найдено");
        }
        return ResponseEntity.ok(imageService.updateImage(id, imageDto));
    }

    // 3) Удаление изображения
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление изображения", description = "Удаляет изображение по ID")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        if (!imageService.existsById(id)) {
            return ResponseEntity.badRequest().body("Изображение с ID " + id + " не найдено");
        }
        imageService.deleteImageById(id);
        return ResponseEntity.ok().build();
    }

    // 4) Получение изображения товара
    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Получение изображений товара", description = "Возвращает все изображения для указанного товара")
    public ResponseEntity<?> getImagesByProduct(@PathVariable UUID productId) {
        Images image = imageService.findImageProduct(productId);
        return ResponseEntity.ok(image);
    }

    // 5) Получение изображения по ID
    @GetMapping("/{id}")
    @Operation(summary = "Получение изображения", description = "Возвращает изображение по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение найдено", content = @Content(mediaType = "image/*")),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    })
    public ResponseEntity<?> getImageById(@PathVariable UUID id) {

        Images image = imageService.getById(id);
        if (image == null || image.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        // Определяем Content-Disposition для автоматической загрузки
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("image_" + id.toString() + ".jpg")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(image.getImage());
    }
}