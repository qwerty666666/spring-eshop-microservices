package com.example.eshop.cart.domain.cart;

import com.example.eshop.sharedkernel.infrastructure.dal.SimpleNaturalIdRepository;

public interface CartRepository extends SimpleNaturalIdRepository<Cart, Long, String> {
}
