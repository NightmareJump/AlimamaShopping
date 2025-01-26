package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<CartItem> cart = cartItemRepository.findByUser(user);
        if (cart.isEmpty()) {
            return ResponseEntity.badRequest().body("购物车为空，无法下单");
        }

        // 计算总价
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart) {
            Product product = cartItem.getProduct();
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalPrice(total);
        order = orderRepository.save(order);

        // 生成订单明细并清空购物车
        for (CartItem cartItem : cart) {
            Product product = cartItem.getProduct();

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(orderItem);

            // 减库存
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // 删除购物车项
            cartItemRepository.delete(cartItem);
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderRepository.findByUserOrderByOrderDateDesc(user));
    }

    @GetMapping("/detail/{orderId}")
    public ResponseEntity<?> getOrderItems(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItemRepository.findByOrder(order));
    }
} 