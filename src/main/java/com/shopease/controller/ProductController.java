package com.shopease.controller;

import com.shopease.entity.Product;
import com.shopease.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public List<Product> search(
            @RequestParam(defaultValue = "") String keyword) {

        return productService.searchProducts(keyword);
    }
}