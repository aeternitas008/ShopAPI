package com.example.shop.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shop.dto.ProductDTO;
import com.example.shop.exception.NotFoundException;
import com.example.shop.model.Product;
import com.example.shop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID existingProductId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private final UUID nonExistingProductId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private final UUID ExistingSupplierId = UUID.fromString("afffffff-ffff-ffff-ffff-ffffffffffff");

    // Вспомогательные методы для создания тестовых данных
    private ProductDTO createValidProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setTitle("Test Product");
        dto.setCategory("Electronics");
        dto.setPrice(1000);
        dto.setAvailableQuantity(10);
        dto.setSupplierId(ExistingSupplierId);
        dto.setLastRestokeDate(LocalDate.now());
        return dto;
    }

    private ProductDTO createInvalidProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setTitle(""); // Невалидное пустое название
        dto.setPrice(-1); // Невалидная цена
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

    @Test
    void addProduct_WithValidData_Returns200() throws Exception {
        // given
        ProductDTO validDto = createValidProductDTO();
        Product createdProduct = createProductEntity();

        when(productService.addProduct(any(ProductDTO.class))).thenReturn(createdProduct);

        // when & then
        mockMvc.perform(post("/api/v1/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());

        verify(productService, times(1)).addProduct(any(ProductDTO.class));
    }

    @Test
    void addProduct_WithInvalidData_Returns400() throws Exception {
        // given
        ProductDTO invalidDto = createInvalidProductDTO();

        // when & then
        mockMvc.perform(post("/api/v1/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        verify(productService, never()).addProduct(any(ProductDTO.class));
    }

    @Test
    void reductionOfProduct_WithExistingProduct_Returns200() throws Exception {
        long count = 5;
        Product updatedProduct = createProductEntity();

        when(productService.decreaseAmountProduct(existingProductId, count)).thenReturn(updatedProduct);

        mockMvc.perform(post("/api/v1/product/decrease/{id}", existingProductId)
                .param("count", String.valueOf(count)))
                .andExpect(status().isOk());

        verify(productService, times(1)).decreaseAmountProduct(existingProductId, count);
    }

    @Test
    void reductionOfProduct_WithNegativeCount_Returns400() throws Exception {
        mockMvc.perform(post("/api/v1/product/decrease/{id}", existingProductId)
                .param("count", "-1"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).existsById(any(UUID.class));
    }

    @Test
    void findProductById_WithExistingProduct_Returns200() throws Exception {
        Product product = createProductEntity();

        when(productService.findById(existingProductId)).thenReturn(product);

        mockMvc.perform(get("/api/v1/product/{id}", existingProductId))
                .andExpect(status().isOk());

        verify(productService, times(1)).findById(existingProductId);
    }

    @Test
    void findProductById_WithNonExistingProduct_Returns404() throws Exception {

        doThrow(NotFoundException.forProduct(ExistingSupplierId)).when(productService).findById(nonExistingProductId);

        mockMvc.perform(get("/api/v1/product/{id}", nonExistingProductId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("не найден")));

        verify(productService, times(1)).findById(any(UUID.class));
    }

    @Test
    void findAllProducts_Returns200() throws Exception {
        List<Product> products = List.of(createProductEntity());

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void findAllProducts_WithEmptyList_Returns200() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void deleteProduct_WithExistingProduct_Returns200() throws Exception {
        doNothing().when(productService).deleteProduct(existingProductId);

        mockMvc.perform(delete("/api/v1/product/{id}", existingProductId))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct(existingProductId);
    }

    @Test
    void deleteProduct_WithNonExistingProduct_Returns404() throws Exception {

        doThrow(NotFoundException.forProduct(nonExistingProductId))
                .when(productService).deleteProduct(nonExistingProductId);
        mockMvc.perform(delete("/api/v1/product/{id}", nonExistingProductId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("не найден")));

        verify(productService).deleteProduct(nonExistingProductId);
    }
}