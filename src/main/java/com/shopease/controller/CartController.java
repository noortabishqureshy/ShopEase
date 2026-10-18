package com.shopease.controller;

import com.shopease.entity.Product;
import com.shopease.service.CartService;
import com.shopease.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(
            CartService cartService,
            ProductService productService) {

        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(id);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "cartError", "Product was not found.");
            return "redirect:/cart";
        }

        if (product.getStock() == null || product.getStock() < 1) {
            redirectAttributes.addFlashAttribute(
                    "cartError", "This product is out of stock.");
            return "redirect:/cart";
        }

        Map<Long, Integer> cart = cartService.getCart(session);
        int currentQuantity = cart.getOrDefault(id, 0);

        if (currentQuantity >= product.getStock()) {
            redirectAttributes.addFlashAttribute(
                    "cartError", "You have reached the available stock.");
            return "redirect:/cart";
        }

        cartService.addProduct(session, id, 1);

        redirectAttributes.addFlashAttribute(
                "cartMessage", "Product added to your cart.");

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(
            HttpSession session,
            Model model) {

        Map<Long, Integer> cart = cartService.getCart(session);

        List<CartLine> cartItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            if (productId == null || quantity == null || quantity <= 0) {
                continue;
            }

            Product product = productService.getProductById(productId);

            if (product == null) {
                continue;
            }

            BigDecimal price = product.getPrice() != null
                    ? product.getPrice()
                    : BigDecimal.ZERO;

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(quantity));

            cartItems.add(new CartLine(product, quantity, subtotal));
            total = total.add(subtotal);
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCart(
            @PathVariable("id") Long id,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<Long, Integer> cart = cartService.getCart(session);

        if (!cart.containsKey(id)) {
            redirectAttributes.addFlashAttribute(
                    "cartError", "Product is not in your cart.");
            return "redirect:/cart";
        }

        if (quantity <= 0) {
            cartService.removeProduct(session, id);
            redirectAttributes.addFlashAttribute(
                    "cartMessage", "Product removed from your cart.");
            return "redirect:/cart";
        }

        Product product = productService.getProductById(id);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "cartError", "Product was not found.");
            return "redirect:/cart";
        }

        if (product.getStock() == null || quantity > product.getStock()) {
            redirectAttributes.addFlashAttribute(
                    "cartError", "Requested quantity exceeds available stock.");
            return "redirect:/cart";
        }

        cartService.updateQuantity(session, id, quantity);

        redirectAttributes.addFlashAttribute(
                "cartMessage", "Cart quantity updated.");

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        cartService.removeProduct(session, id);

        redirectAttributes.addFlashAttribute(
                "cartMessage", "Product removed from your cart.");

        return "redirect:/cart";
    }

    public static class CartLine {

        private final Product product;
        private final int quantity;
        private final BigDecimal subtotal;

        public CartLine(
                Product product,
                int quantity,
                BigDecimal subtotal) {

            this.product = product;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }
}
