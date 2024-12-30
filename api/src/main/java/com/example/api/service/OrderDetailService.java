package com.example.api.service;

import com.example.api.entity.Dessert;
import com.example.api.entity.Order;
import com.example.api.entity.OrderDetail;
import com.example.api.repository.DessertRepository;
import com.example.api.repository.OrderDetailRepository;
import com.example.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DessertRepository dessertRepository;

    public OrderDetail create(Long orderId, Long dessertId, Long quantity){
        Order order = orderRepository.getById(orderId);
        Dessert dessert = dessertRepository.getById(dessertId);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setDessert(dessert);
        orderDetail.setQuantity(quantity);

        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> list(){
        return orderDetailRepository.findAll();
    }

    public void delete(Long id){
        orderDetailRepository.deleteById(id);
    }

    public OrderDetail get(Long id){
        return orderDetailRepository.getReferenceById(id);
    }

    public OrderDetail update(Long id, Long quantity){
        OrderDetail orderDetail = orderDetailRepository.getReferenceById(id);
        if(quantity != 0){
            orderDetail.setQuantity(quantity);
        }
        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> sortByOrder(Long orderId){
        if(orderId != null){
            return orderDetailRepository.findByOrderId(orderId);
        }
        return orderDetailRepository.findAll();
    }
}
