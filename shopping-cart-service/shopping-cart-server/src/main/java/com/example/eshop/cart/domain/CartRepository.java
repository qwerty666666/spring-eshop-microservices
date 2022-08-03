package com.example.eshop.cart.domain;

import com.example.eshop.springdatajpautils.SimpleNaturalIdRepository;

public interface CartRepository extends SimpleNaturalIdRepository<Cart, Long, String> {
}
