package com.example.api.repository;

import com.example.api.entity.Order;
import com.example.api.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    Optional<OrderDetail> findById(Long id);

    List<OrderDetail> findByOrderId(Long orderId);
}
