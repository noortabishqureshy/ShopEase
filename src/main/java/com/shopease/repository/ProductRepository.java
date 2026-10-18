package com.shopease.repository;

import com.shopease.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository
                extends JpaRepository<Product, Long> {

        @Query("""
                        SELECT p FROM Product p
                        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(COALESCE(p.description, ''))
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                        ORDER BY p.id DESC
                        """)
        List<Product> searchProducts(
                        @Param("keyword") String keyword);
}