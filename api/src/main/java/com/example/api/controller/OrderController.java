package com.example.api.controller;

import com.example.api.entity.Order;
import com.example.api.service.OrderService;
import com.example.api.state.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> list(){
        List<Order> orders = orderService.list();
        if(orders != null)
            return new ResponseEntity<>(orders, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<Order>> listByUser(@RequestParam Long userId){
        List<Order> orders = orderService.listByUser(userId);
        if(orders != null)
            return new ResponseEntity<>(orders, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/add")
    public ResponseEntity<Order> create(@RequestParam Long userId){
        Order order = orderService.create(userId);
        if(order!= null)
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam Long orderId){
        Order order = orderService.get(orderId);
        if(order != null){
            orderService.delete(orderId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping
    public ResponseEntity<Order> update(@RequestParam Long orderId, @RequestParam boolean optionState){
        Order order = orderService.update(orderId, optionState);
        if(order != null)
            return new ResponseEntity<>(order, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> get(@PathVariable Long orderId){
        Order order = orderService.get(orderId);
        if(order != null)
            return new ResponseEntity<>(order, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/addProcessId")
    public ResponseEntity<Order> addProcessId(@RequestParam Long orderId, @RequestParam String processId){
        Order order = orderService.addProcessId(orderId, processId);
        if(order != null){
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getProcessId")
    public ResponseEntity<String> getProcessId(@RequestParam Long orderId){
        Order order = orderService.get(orderId);
        if(order != null){
            String processId = orderService.getProcessId(orderId);
            return new ResponseEntity<>(processId, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getStatus")
    public ResponseEntity<String> getStatus(@RequestParam Long orderId){
        Order order = orderService.get(orderId);
        if(order != null){
            OrderStatus processId = orderService.getStatus(orderId);
            return new ResponseEntity<>(processId.toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
