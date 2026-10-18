package com.shopease.controller;

import com.shopease.entity.Product;
import com.shopease.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String products(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts());

        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {

        model.addAttribute("product", new Product());

        return "admin/product-form";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @PostMapping("/products")
    public String createProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/product-form";
        }

        productService.saveProduct(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "product",
                productService.getProductById(id));

        return "admin/product-form";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") Product submitted,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/product-form";
        }

        Product existing = productService.getProductById(id);

        existing.setName(submitted.getName());
        existing.setDescription(submitted.getDescription());
        existing.setPrice(submitted.getPrice());
        existing.setStock(submitted.getStock());
        existing.setImageUrl(submitted.getImageUrl());

        productService.saveProduct(existing);

        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return "redirect:/admin/products";
    }
}