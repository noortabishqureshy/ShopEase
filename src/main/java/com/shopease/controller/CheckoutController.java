package com.shopease.controller;

import com.shopease.dto.CheckoutRequest;
import com.shopease.entity.Order;
import com.shopease.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {

        model.addAttribute(
                "checkoutRequest",
                new CheckoutRequest());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            @Valid @ModelAttribute("checkoutRequest") CheckoutRequest request,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            return "checkout";
        }

        try {

            Order order = orderService.placeOrder(
                    request,
                    session);

            return "redirect:/order-success/" + order.getId();

        } catch (IllegalArgumentException
                | IllegalStateException exception) {

            model.addAttribute(
                    "checkoutError",
                    exception.getMessage());

            return "checkout";
        }
    }

    @GetMapping("/order-success/{id}")
    public String orderSuccess(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("orderId", id);

        return "order-success";
    }
}