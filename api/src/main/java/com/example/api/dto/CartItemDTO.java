package com.example.api.dto;

import com.example.api.entity.Category;
import com.example.api.entity.Dessert;

public class CartItemDTO {
    private int quantity;
    private Dessert dessert;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Dessert getDessert() {
        return dessert;
    }

    public void setDessert(Dessert dessert) {
        this.dessert = dessert;
    }
}
