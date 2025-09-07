package com.example.shop.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
import com.example.shop.exception.NotEnoughGoodException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.model.Images;
import com.example.shop.model.Product;
import com.example.shop.model.Supplier;
import com.example.shop.repository.ProductRepository;

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
        image.setId(UUID.fromString("d4e5f6b7-a8c9-0123-defb-456789012345"));
        image.setImage("test-image".getBytes());
        return image;
    }

    @Test
    void addProduct_WithValidData_ShouldSaveAndReturnProduct() {
        ProductDTO productDTO = createValidProductDTO();
        Product productEntity = createProductEntity();
        Supplier supplier = createSupplierEntity();

        when(productMapper.toEntity(productDTO)).thenReturn(productEntity);
        when(supplierService.findById(supplierId)).thenReturn(supplier);
        when(productRepository.save(productEntity)).thenReturn(productEntity);

        Product result = productService.addProduct(productDTO);

        assertNotNull(result);
        assertEquals(existingProductId, result.getId());
        assertEquals("Test Product", result.getTitle());
        assertEquals(supplier, result.getSupplier());

        verify(productMapper).toEntity(productDTO);
        verify(supplierService).findById(supplierId);
        verify(productRepository).save(productEntity);
    }

    @Test
    void decreaseAmountProduct_WithSufficientQuantity_ShouldDecreaseQuantity() {
        Product product = createProductEntity();
        product.setAvailableQuantity(10);

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.decreaseAmountProduct(existingProductId, 3);

        assertNotNull(result);
        assertEquals(7, result.getAvailableQuantity());

        verify(productRepository).findById(existingProductId);
        verify(productRepository).save(product);
    }

    @Test
    void decreaseAmountProduct_WithInsufficientQuantity_ShouldThrowNotEnoughGoodException() {
        Product product = createProductEntity();
        product.setAvailableQuantity(5);

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        assertThrows(NotEnoughGoodException.class,
                () -> productService.decreaseAmountProduct(existingProductId, 10));

        verify(productRepository).findById(existingProductId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void findById_WithExistingProduct_ShouldReturnProduct() {
        Product product = createProductEntity();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        Product result = productService.findById(existingProductId);

        assertNotNull(result);
        assertEquals(existingProductId, result.getId());

        verify(productRepository).findById(existingProductId);
    }

    @Test
    void findById_WithNonExistingProduct_ShouldThrowNotFoundException() {
        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> productService.findById(nonExistingProductId));

        verify(productRepository).findById(nonExistingProductId);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        List<Product> products = List.of(createProductEntity());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(productRepository).findAll();
    }

    @Test
    void deleteProduct_WithExistingProduct_ShouldCallRepositoryDelete() {
        when(productRepository.existsById(existingProductId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(existingProductId);

        productService.deleteProduct(existingProductId);

        verify(productRepository).existsById(existingProductId);
        verify(productRepository).deleteById(existingProductId);
    }

    @Test
    void deleteProduct_WithNonExistingProduct_ShouldThrowNotFoundException() {
        when(productRepository.existsById(nonExistingProductId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> productService.deleteProduct(nonExistingProductId));

        verify(productRepository).existsById(nonExistingProductId);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void updateImage_WithExistingProduct_ShouldUpdateImage() {
        Product product = createProductEntity();
        Images image = createImageEntity();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.updateImage(existingProductId, image);

        assertNotNull(result);
        assertEquals(image, result.getImage());

        verify(productRepository).findById(existingProductId);
        verify(productRepository).save(product);
    }

    @Test
    void getImage_WithExistingProductWithImage_ShouldReturnImage() {
        Product product = createProductEntity();
        Images image = createImageEntity();
        product.setImage(image);

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));

        Images result = productService.getImage(existingProductId);

        assertNotNull(result);
        assertEquals(image, result);

        verify(productRepository).findById(existingProductId);
    }

    @Test
    void existsById_WithExistingProduct_ShouldNotThrowException() {
        when(productRepository.existsById(existingProductId)).thenReturn(true);

        assertDoesNotThrow(() -> productService.existsById(existingProductId));

        verify(productRepository).existsById(existingProductId);
    }

    @Test
    void existsById_WithNonExistingProduct_ShouldThrowNotFoundException() {
        when(productRepository.existsById(nonExistingProductId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> productService.existsById(nonExistingProductId));

        verify(productRepository).existsById(nonExistingProductId);
    }
}
