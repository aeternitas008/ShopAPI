package com.example.shop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dto.ProductDTO;
import com.example.shop.exception.InsufficientStockException;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Image;
import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // 1) Добавление товара
    public Product addProduct(ProductDTO productDto) {
        Product product = productMapper.toEntity(productDto);
        return productRepository.save(product);
    }

    // 2) Уменьшение количества товара
    @Transactional
    public Product decreaseAmountProduct(UUID productId, long requsted) {
        Product product = findById(productId);
        long currentQuantity = product.getAvailableQuantity();
        if (currentQuantity < requsted) {
            throw new InsufficientStockException(
                    productId, currentQuantity, requsted);
        }
        product.setAvailableQuantity(product.getAvailableQuantity() - requsted);
        return productRepository.save(product);
    }

    // 3) Получение товара по id
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    // 4) Получение всех товаров
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 5) Удаление товара
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    // 6) Обновление картинки
    public Product updateImage(UUID id, Image image) {
        Product product = findById(id);
        product.setImage(image);
        return productRepository.save(product);
    }

    // 7) Удаление товара
    public Image getImage(UUID id) {
        return findById(id).getImage();
    }

    public boolean existsById(UUID id) {
        return productRepository.existsById(id);
    }
}