package com.example.demo.repository;

import com.example.demo.model.CartItem;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    CartItem findByUserIdAndProductId(Long userId, Long productId);
    CartItem findByUserAndProduct(User user, Product product);
} 