package com.example.shop.service;

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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.shop.dto.ProductDTO;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Images;
import com.example.shop.model.Product;
import com.example.shop.model.Supplier;
import com.example.shop.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private ProductService productService;

    private final UUID existingProductId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingProductId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private final UUID supplierId = UUID.fromString("c3d4e5f6-f7b8-9012-cdef-345678901234");

    private ProductDTO createValidProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setTitle("Test Product");
        dto.setCategory("Electronics");
        dto.setPrice(1000);
        dto.setAvailableQuantity(10);
        dto.setSupplierId(supplierId);
        return dto;
    }

    private Product createProductEntity() {
        Product product = new Product();
        product.setId(existingProductId);
        product.setTitle("Test Product");
        product.setCategory("Electronics");
        product.setPrice(1000);
        product.setAvailableQuantity(10);
        return product;
    }

    private Supplier createSupplierEntity() {
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");
        return supplier;
    }

    private Images createImageEntity() {
        Images image = new Images();
        image.setId(UUID.fromString("d4e5f6g7-h8i9-0123-defg-456789012345"));
        image.setImage("test-image".getBytes());
        return image;
    }

    @Test
    void addProduct_WithValidData_ShouldSaveAndReturnProduct() {
        // given
        ProductDTO productDTO = createValidProductDTO();
        Product productEntity = createProductEntity();
        Supplier supplier = createSupplierEntity();

        when(productMapper.toEntity(productDTO)).thenReturn(productEntity);
        when(supplierService.getById(supplierId)).thenReturn(supplier);
        when(productRepository.save(productEntity)).thenReturn(productEntity);

        // when
        Product result = productService.addProduct(productDTO);

        // then
        assertNotNull(result);
        assertEquals(existingProductId, result.getId());
        assertEquals("Test Product", result.getTitle());
        assertEquals(supplier, result.getSupplier());

        verify(productMapper, times(1)).toEntity(productDTO);
        verify(supplierService, times(1)).getById(supplierId);
        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    void decreaseAmountProduct_WithSufficientQuantity_ShouldDecreaseQuantity() {
        // given
        Product product = createProductEntity();
        product.setAvailableQuantity(10);
        long requested = 3;

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // when
        Product result = productService.decreaseAmountProduct(existingProductId, requested);

        // then
        assertNotNull(result);
        assertEquals(7, result.getAvailableQuantity());

        verify(productRepository, times(1)).findById(existingProductId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void decreaseAmountProduct_WithInsufficientQuantity_ShouldThrowBadRequestException() {
        // given
        Product product = createProductEntity();
        product.setAvailableQuantity(5);
        long requested = 10;

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        // when & then
        assertThrows(BadRequestException.class, () -> {
            productService.decreaseAmountProduct(existingProductId, requested);
        });

        verify(productRepository, times(1)).findById(existingProductId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void findById_WithExistingProduct_ShouldReturnProduct() {
        // given
        Product product = createProductEntity();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        // when
        Product result = productService.findById(existingProductId);

        // then
        assertNotNull(result);
        assertEquals(existingProductId, result.getId());

        verify(productRepository, times(1)).findById(existingProductId);
    }

    @Test
    void findById_WithNonExistingProduct_ShouldThrowEntityNotFoundException() {
        // given
        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            productService.findById(nonExistingProductId);
        });

        verify(productRepository, times(1)).findById(nonExistingProductId);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // given
        List<Product> products = List.of(createProductEntity());

        when(productRepository.findAll()).thenReturn(products);

        // when
        List<Product> result = productService.getAllProducts();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void deleteProduct_WithExistingProduct_ShouldCallRepositoryDelete() {
        // given
        doNothing().when(productRepository).deleteById(existingProductId);

        // when
        productService.deleteProduct(existingProductId);

        // then
        verify(productRepository, times(1)).deleteById(existingProductId);
    }

    @Test
    void updateImage_WithExistingProduct_ShouldUpdateImage() {
        // given
        Product product = createProductEntity();
        Images image = createImageEntity();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // when
        Product result = productService.updateImage(existingProductId, image);

        // then
        assertNotNull(result);
        assertEquals(image, result.getImage());

        verify(productRepository, times(1)).findById(existingProductId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getImage_WithExistingProductWithImage_ShouldReturnImage() {
        // given
        Product product = createProductEntity();
        Images image = createImageEntity();
        product.setImage(image);

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        // when
        Images result = productService.getImage(existingProductId);

        // then
        assertNotNull(result);
        assertEquals(image, result);

        verify(productRepository, times(1)).findById(existingProductId);
    }

    @Test
    void getProductById_WithExistingProduct_ShouldReturnProduct() {
        // given
        Product product = createProductEntity();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        // when
        Product result = productService.getProductById(existingProductId);

        // then
        assertNotNull(result);
        assertEquals(existingProductId, result.getId());

        verify(productRepository, times(1)).findById(existingProductId);
    }

    @Test
    void getProductById_WithNonExistingProduct_ShouldThrowNotFoundException() {
        // given
        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            productService.getProductById(nonExistingProductId);
        });

        verify(productRepository, times(1)).findById(nonExistingProductId);
    }

    @Test
    void existsById_WithExistingProduct_ShouldNotThrowException() {
        // given
        when(productRepository.existsById(existingProductId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> {
            productService.existsById(existingProductId);
        });

        verify(productRepository, times(1)).existsById(existingProductId);
    }

    @Test
    void existsById_WithNonExistingProduct_ShouldThrowNotFoundException() {
        // given
        when(productRepository.existsById(nonExistingProductId)).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            productService.existsById(nonExistingProductId);
        });

        verify(productRepository, times(1)).existsById(nonExistingProductId);
    }

    @Test
    void decreaseAmountProductValidated_WithValidData_ShouldDecreaseQuantity() {
        // given
        Product product = createProductEntity();
        product.setAvailableQuantity(10);
        long count = 3;

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // when
        Product result = productService.decreaseAmountProductValidated(existingProductId, count);

        // then
        assertNotNull(result);
        assertEquals(7, result.getAvailableQuantity());

        verify(productRepository, times(1)).findById(existingProductId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void decreaseAmountProductValidated_WithZeroCount_ShouldThrowBadRequestException() {
        // given
        long count = 0;

        // when & then
        assertThrows(BadRequestException.class, () -> {
            productService.decreaseAmountProductValidated(existingProductId, count);
        });

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void decreaseAmountProductValidated_WithNegativeCount_ShouldThrowBadRequestException() {
        // given
        long count = -5;

        // when & then
        assertThrows(BadRequestException.class, () -> {
            productService.decreaseAmountProductValidated(existingProductId, count);
        });

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void decreaseAmountProductValidated_WithInsufficientQuantity_ShouldThrowBadRequestException() {
        // given
        Product product = createProductEntity();
        product.setAvailableQuantity(5);
        long count = 10;

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        // when & then
        assertThrows(BadRequestException.class, () -> {
            productService.decreaseAmountProductValidated(existingProductId, count);
        });

        verify(productRepository, times(1)).findById(existingProductId);
        verify(productRepository, never()).save(any());
    }
}