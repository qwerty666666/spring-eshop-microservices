package com.example.eshop.cart.domain;

import com.example.eshop.sharedkernel.infrastructure.dal.SimpleNaturalIdRepository;

public interface CartRepository extends SimpleNaturalIdRepository<Cart, Long, String> {
}
