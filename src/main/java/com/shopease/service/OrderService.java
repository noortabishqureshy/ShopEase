package com.shopease.service;

import com.shopease.dto.CheckoutRequest;
import com.shopease.entity.Order;
import com.shopease.entity.OrderItem;
import com.shopease.entity.Product;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CartService cartService) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order placeOrder(
            CheckoutRequest request,
            HttpSession session) {

        Map<Long, Integer> cart = cartService.getCart(session);

        if (cart.isEmpty()) {
            throw new IllegalStateException(
                    "Your cart is empty");
        }

        Order order = new Order();

        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setStatus("PLACED");

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found"));

            if (quantity == null || quantity < 1) {
                throw new IllegalArgumentException(
                        "Invalid product quantity");
            }

            if (product.getStock() == null
                    || product.getStock() < quantity) {

                throw new IllegalStateException(
                        "Insufficient stock for "
                                + product.getName());
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            total = total.add(itemTotal);

            OrderItem item = new OrderItem();

            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);

            order.addItem(item);

            product.setStock(product.getStock() - quantity);
        }

        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(session);

        return savedOrder;
    }
}