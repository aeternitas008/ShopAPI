package com.example.shop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dto.ProductDTO;
import com.example.shop.exception.NotEnoughGoodException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Images;
import com.example.shop.model.Product;
import com.example.shop.model.Supplier;
import com.example.shop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final SupplierService supplierService;

    // 1) Добавление товара
    public Product addProduct(ProductDTO productDto) {
        Product product = productMapper.toEntity(productDto);
        Supplier supplier = supplierService.findById(productDto.getSupplierId());
        product.setSupplier(supplier);
        return productRepository.save(product);
    }

    // 2) Уменьшение количества товара
    @Transactional
    public Product decreaseAmountProduct(UUID productId, long requested) {
        Product product = findById(productId);
        long currentQuantity = product.getAvailableQuantity();

        if (requested <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше 0");
        }

        if (currentQuantity < requested) {
            throw new NotEnoughGoodException("Недостаточно товара в наличии", currentQuantity, requested);
        }

        product.setAvailableQuantity(currentQuantity - requested);
        return productRepository.save(product);
    }

    // 3) Получение товара по id
    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(
                () -> NotFoundException.forProduct(id));
    }

    // 4) Получение всех товаров
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 5) Удаление товара
    public void deleteProduct(UUID id) {
        existsById(id);
        productRepository.deleteById(id);
    }

    // 6) Обновление картинки
    public Product updateImage(UUID id, Images image) {
        Product product = findById(id);
        product.setImage(image);
        return productRepository.save(product);
    }

    // 7) Удаление товара
    public Images getImage(UUID id) {
        return findById(id).getImage();
    }

    public void existsById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw NotFoundException.forProduct(id);
        }
    }
}