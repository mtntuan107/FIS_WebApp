package com.example.api.controller;

import com.example.api.dto.CartDTO;
import com.example.api.dto.CartItemDTO;
import com.example.api.entity.Dessert;
import com.example.api.service.DessertService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")


public class CartController {
    @Autowired
    private DessertService dessertService;


    @GetMapping("/add/{dessertId}")
    public ResponseEntity<CartDTO> addToCart(HttpSession session, @PathVariable(name = "dessertId") Long id) {
        Dessert dessert = dessertService.get(id);
        if (session.getAttribute("cart") == null) {
            CartDTO cart = new CartDTO();
            CartItemDTO item = new CartItemDTO();
            item.setQuantity(1);
            item.setDessert(dessert);
            List<CartItemDTO> items = new ArrayList<>();
            items.add(item);
            cart.setItems(items);

            session.setAttribute("cart", cart);
        } else {
            CartDTO cart = (CartDTO) session.getAttribute("cart");
            List<CartItemDTO> items = cart.getItems();
            boolean flag = false;

            for (CartItemDTO item : items) {
                if (item.getDessert().getId().equals(dessert.getId())) {
                    item.setQuantity(item.getQuantity() + 1);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                CartItemDTO item = new CartItemDTO();
                item.setQuantity(1);
                item.setDessert(dessert);
                items.add(item);
            }

            cart.setItems(items);
            session.setAttribute("cart", cart);
        }

        CartDTO cart = (CartDTO) session.getAttribute("cart");
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
    @GetMapping
    public CartDTO getCart(HttpSession session) {
        CartDTO cart = (CartDTO) session.getAttribute("cart");
        if (cart != null) {

        } else {
            cart = new CartDTO();
        }
        return cart;
    }

    @GetMapping("/get")
    public ResponseEntity<CartDTO> getCartDetails(HttpSession session) {
        CartDTO cart = (CartDTO) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartDTO(); // Trả về giỏ hàng trống nếu session chưa có giỏ
        }
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<CartDTO> removeFromCart(HttpSession session, @RequestParam Long dessertId) {
        CartDTO cart = (CartDTO) session.getAttribute("cart");

        if (cart != null) {
            List<CartItemDTO> items = cart.getItems();
            items.removeIf(item -> item.getDessert().getId().equals(dessertId)); // Xóa item dựa trên dessertId
            cart.setItems(items);
            session.setAttribute("cart", cart);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Trả về 404 nếu không có giỏ hàng trong session
        }

        return new ResponseEntity<>(cart, HttpStatus.OK); // Trả về giỏ hàng sau khi xóa
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartDTO> clearCart(HttpSession session) {

        CartDTO cart = (CartDTO) session.getAttribute("cart");

        if (cart != null) {
            cart.setItems(new ArrayList<>());
            session.setAttribute("cart", cart);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
