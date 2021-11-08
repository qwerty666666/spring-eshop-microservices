package com.example.eshop.sales.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * Provides info about Shipping.
 */
@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {
    @Column(name = "delivery_id", nullable = false)
    private String id;

    @Column(name = "delivery_name", nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "delivery_price", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "delivery_price_currency", nullable = false))
    private Money price;

    public Delivery(String id, String name, Address address, Money price) {
        Assertions.notEmpty(id, "id must be not empty");
        Assertions.notEmpty(name, "name must be not empty");
        Assertions.notNull(address, "address must be not null");
        Assertions.notNull(price, "price must be not null");

        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
    }
}
