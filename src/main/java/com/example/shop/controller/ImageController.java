package com.example.shop.controller;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import com.example.shop.model.Image;
import com.example.shop.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    // 1) Добавление изображения
    @PostMapping("/add")
    @Operation(summary = "Добавление изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    public ResponseEntity<?> addImage(
            @Parameter(description = "ID товара", required = true) @RequestParam UUID productId,

            @Parameter(description = "Изображение в виде byte array", required = true) @RequestBody ImageDTO imageDto) {

        try {

            Image image = imageService.updateImageProduct(productId, imageDto);

            return ResponseEntity.ok(image.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2) Изменение изображения
    @PatchMapping("/update/{id}")
    @Operation(summary = "Обновление изображения")
    public ResponseEntity<?> updateImage(
            @Parameter(description = "ID изображения", required = true) @PathVariable UUID id,

            @Parameter(description = "Новые данные изображения", required = true) @RequestBody ImageDTO imageDto) {

        if (!imageService.existsById(id)) {
            return ResponseEntity.badRequest().body("Изображение с ID " + id + " не найдено");
        }

        return ResponseEntity.ok(imageService.updateImage(id, imageDto));
    }

    // 3) Удаление изображения
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление изображения", description = "Удаляет изображение по ID")
    public ResponseEntity<?> deleteImage(
            @Parameter(description = "ID изображения", required = true) @PathVariable UUID id) {

        if (!imageService.existsById(id)) {
            return ResponseEntity.badRequest().body("Изображение с ID " + id + " не найдено");
        }
        imageService.deleteImageById(id);
        return ResponseEntity.ok().build();
    }

    // 4) Получение изображения товара
    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Получение изображений товара", description = "Возвращает все изображения для указанного товара")
    public ResponseEntity<?> getImagesByProduct(
            @Parameter(description = "ID товара", required = true) @PathVariable UUID productId) {

        Image image = imageService.findImageProduct(productId);
        return ResponseEntity.ok(image);
    }

    // 5) Получение изображения по ID
    @GetMapping("/{id}")
    @Operation(summary = "Получение изображения", description = "Возвращает изображение по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение найдено", content = @Content(mediaType = "image/*")),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    })
    public ResponseEntity<?> getImageById(
            @Parameter(description = "ID изображения", required = true) @PathVariable UUID id) {

        byte[] imageData = imageService.getById(id).getImage();
        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // или MediaType.IMAGE_PNG
                .body(imageData);
    }

    private Map<String, String> getErrorMessages(BindingResult result) {
        return result.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing + ", " + replacement));
    }
}