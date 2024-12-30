package com.example.api.controller;

import com.example.api.entity.OrderDetail;
import com.example.api.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-detail")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<List<OrderDetail>> list(){
        List<OrderDetail> orderDetails = orderDetailService.list();
        if(orderDetails != null){
            return new ResponseEntity<>(orderDetails, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/add")
    public ResponseEntity<OrderDetail> create(@RequestParam Long orderId,
                                              @RequestParam Long dessertId,
                                              @RequestParam Long quantity){
        OrderDetail orderDetail = orderDetailService.create(orderId, dessertId, quantity);
        if(orderDetail != null){
            return new ResponseEntity<>(orderDetail, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam Long id){
        OrderDetail orderDetail = orderDetailService.get(id);
        if(orderDetail != null){
            orderDetailService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PutMapping
    public ResponseEntity<OrderDetail> update(@RequestParam Long id, @RequestParam Long quantity){
        OrderDetail orderDetail = orderDetailService.update(id, quantity);
        if(orderDetail != null)
            return new ResponseEntity<>(orderDetail, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetail> get(@PathVariable Long id){
        OrderDetail orderDetail = orderDetailService.get(id);
        if(orderDetail != null)
            return new ResponseEntity<>(orderDetail, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/order")
    public ResponseEntity<List<OrderDetail>> list(@RequestParam(required = false) Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.sortByOrder(orderId);
        if (orderDetails != null && !orderDetails.isEmpty()) {
            return new ResponseEntity<>(orderDetails, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
