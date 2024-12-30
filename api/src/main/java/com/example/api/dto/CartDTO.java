package com.example.api.dto;

import com.example.api.entity.Dessert;

import java.util.List;
import java.util.Map;

public class CartDTO {
    private List<CartItemDTO> items;
    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}
