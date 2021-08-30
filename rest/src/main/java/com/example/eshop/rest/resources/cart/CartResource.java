package com.example.eshop.rest.resources.cart;

import java.util.List;

public record CartResource(String id, List<CartItemResource> items) {
}
