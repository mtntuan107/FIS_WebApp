package com.example.api.service;

import com.example.api.entity.Order;
import com.example.api.entity.User;
import com.example.api.repository.OrderRepository;
import com.example.api.repository.UserRepository;
import com.example.api.state.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public Order create(Long userId){
        Optional<User> user = userRepository.findById(userId);

        Order order = new Order();
        order.setUser(user.get());
        order.setStatus(OrderStatus.PENDING);
        order.setDate(LocalDateTime.now());
        order.setProcessId(null);
        return orderRepository.save(order);
    }

    public List<Order> list(){
        return orderRepository.findAll();
    }

    public void delete(Long orderId){
        orderRepository.deleteById(orderId);
    }

    public Order get(Long orderId){
        return orderRepository.getReferenceById(orderId);
    }

    public Order update(Long orderId, boolean optionState){
        Order order = orderRepository.getReferenceById(orderId);
        if(optionState){
            if(order.getStatus() == OrderStatus.PENDING)
                order.setStatus(OrderStatus.PROCESSING);
            else if(order.getStatus() == OrderStatus.PROCESSING)
                order.setStatus(OrderStatus.COMPLETED);
            return orderRepository.save(order);
        }
        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    public List<Order> listByUser(Long userId){
        List<Order> orders = orderRepository.findAllByUser(userRepository.findById(userId));
        return orders;
    }

    public Order addProcessId(Long orderId,String processId){
        Order order = orderRepository.getReferenceById(orderId);
        if(processId != null){
            order.setProcessId(processId);
        }
        return orderRepository.save(order);
    }

    public String getProcessId(Long orderId){
        Order order = orderRepository.getReferenceById(orderId);
        if(order != null){
            return order.getProcessId();
        }
        return null;
    }
    public OrderStatus getStatus(Long orderId){
        Order order = orderRepository.getReferenceById(orderId);
        if(order != null){
            return order.getStatus();
        }
        return null;
    }
}
