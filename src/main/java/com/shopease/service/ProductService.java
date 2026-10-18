package com.shopease.service;

import com.shopease.entity.Product;
import com.shopease.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + id));
    }

    public List<Product> searchProducts(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }

        return productRepository.searchProducts(
                keyword.trim());
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}