package com.shopease.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CartService {

    private static final String CART_KEY = "SHOPPING_CART";

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getCart(HttpSession session) {
        Object storedCart = session.getAttribute(CART_KEY);

        if (storedCart instanceof Map<?, ?>) {
            return (Map<Long, Integer>) storedCart;
        }

        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(CART_KEY, cart);
        return cart;
    }

    public void addProduct(
            HttpSession session,
            Long productId,
            int quantity) {

        if (productId == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Invalid product or quantity.");
        }

        Map<Long, Integer> cart = getCart(session);

        int currentQuantity = cart.getOrDefault(productId, 0);

        cart.put(productId, Math.addExact(currentQuantity, quantity));
    }

    public void removeProduct(
            HttpSession session,
            Long productId) {

        getCart(session).remove(productId);
    }

    public void updateQuantity(
            HttpSession session,
            Long productId,
            int quantity) {

        if (productId == null) {
            throw new IllegalArgumentException("Invalid product ID.");
        }

        Map<Long, Integer> cart = getCart(session);

        if (!cart.containsKey(productId)) {
            throw new IllegalArgumentException(
                    "Product is not in the cart.");
        }

        if (quantity <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, quantity);
        }
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }
}
