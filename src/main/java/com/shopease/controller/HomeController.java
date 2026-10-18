package com.shopease.controller;

import com.shopease.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts());

        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        model.addAttribute(
                "products",
                productService.searchProducts(keyword));

        model.addAttribute("keyword", keyword);

        return "index";
    }
}