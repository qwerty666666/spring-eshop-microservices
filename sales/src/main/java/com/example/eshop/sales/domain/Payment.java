package com.example.eshop.sales.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Provides info about Payment for {@link Order}
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {
    @Column(name = "payment_id", nullable = false)
    private String id;

    @Column(name = "payment_name", nullable = false)
    private String name;

    public Payment(String id, String name) {
        Assertions.notEmpty(id, "id must be not empty");
        Assertions.notEmpty(name, "nane must be not empty");

        this.id = id;
        this.name = name;
    }
}
