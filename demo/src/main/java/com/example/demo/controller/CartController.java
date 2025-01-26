package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartItemRepository.findByUser(user));
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                                     @RequestParam Long productId,
                                     @RequestParam int quantity) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        
        if (user == null || product == null) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = cartItemRepository.findByUserAndProduct(user, product);
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
        }
        return ResponseEntity.ok(cartItemRepository.save(item));
    }

    @PutMapping
    public ResponseEntity<?> updateQuantity(@RequestParam Long userId,
                                          @RequestParam Long productId,
                                          @RequestParam int quantity) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        
        if (user == null || product == null) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = cartItemRepository.findByUserAndProduct(user, product);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        
        item.setQuantity(quantity);
        return ResponseEntity.ok(cartItemRepository.save(item));
    }

    @DeleteMapping
    public ResponseEntity<?> removeItem(@RequestParam Long userId,
                                      @RequestParam Long productId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        
        if (user == null || product == null) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = cartItemRepository.findByUserAndProduct(user, product);
        if (item != null) {
            cartItemRepository.delete(item);
        }
        return ResponseEntity.ok().build();
    }
} 