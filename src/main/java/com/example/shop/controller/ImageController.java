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
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Images;
import com.example.shop.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 1) Добавление изображения
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Добавление изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Изображение успешно добавлено"),
            @ApiResponse(responseCode = "404", description = "Товар не найден", content = {}),
            @ApiResponse(responseCode = "415", description = "Неподдерживаемый тип данных"),
    })
    public ResponseEntity<UUID> addImage(
            @RequestParam @Valid UUID productId,
            @RequestBody @Valid byte[] byteArray) {

        if (byteArray == null) {
            throw new NotFoundException("Пустой массив изображения");
        }
        ImageDTO imageDto = ImageDTO.builder()
                .image(byteArray)
                .build();
        Images image = imageService.updateImageProduct(productId, imageDto);
        return ResponseEntity.ok(image.getId());
    }

    // 2) Изменение изображения
    @PatchMapping("/{id}")
    @Operation(summary = "Обновление изображения")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "415", description = "Неподдерживаемый тип данных"),
    })
    public ResponseEntity<Images> updateImage(@PathVariable UUID id, @RequestBody @Valid ImageDTO imageDto) {
        imageService.existsById(id);
        return ResponseEntity.ok(imageService.updateImage(id, imageDto));
    }

    // 3) Удаление изображения
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление изображения", description = "Удаляет изображение по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
    })
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id) {
        imageService.existsById(id);
        imageService.deleteImageById(id);
        return ResponseEntity.ok().build();
    }

    // 4) Получение изображения товара
    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Получение изображений товара", description = "Возвращает все изображения для указанного товара")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Изображение для товара не найдено"),
    })
    public ResponseEntity<Images> getImagesByProduct(@PathVariable @Valid UUID productId) {
        Images image = imageService.findImageProduct(productId);
        if (image == null) {
            throw new NotFoundException("Изображение для товара с ID " + productId + " не найдено");
        }
        return ResponseEntity.ok(image);
    }

    // 5) Получение изображения по ID
    @GetMapping("/{id}")
    @Operation(summary = "Получение изображения", description = "Возвращает изображение по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
    })
    public ResponseEntity<byte[]> getImageById(@PathVariable @Valid UUID id) {
        Images image = imageService.getImageById(id);

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("image_" + id.toString() + ".jpg")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(image.getImage());
    }
}