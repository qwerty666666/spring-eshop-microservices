package com.example.eshop.sales.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * Provides info about Customer and Delivery Address
 */
@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Embedded
    private Phone phone;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street")
    @Nullable
    private String street;

    @Column(name = "building", nullable = false)
    private String building;

    @Column(name = "flat")
    @Nullable
    private String flat;

    public Address(String fullname, Phone phone, String country, String city, @Nullable String street, String building, @Nullable String flat) {
        Assertions.notEmpty(fullname, "fullname must be non empty");
        Assertions.notNull(phone, "phone must be non null");
        Assertions.notEmpty(country, "country must be non empty");
        Assertions.notEmpty(city, "city must be non empty");
        Assertions.notEmpty(building, "building must be non empty");

        this.fullname = fullname;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.street = street;
        this.building = building;
        this.flat = flat;
    }
}
