package com.example.api.repository;

import com.example.api.entity.Dessert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DessertRepository extends JpaRepository<Dessert, Long> {
    Optional<Dessert> findById(Long id);

    List<Dessert> findByNameContainingIgnoreCase(String name);

    List<Dessert> findByCategoryId(Long categoryId);
}

