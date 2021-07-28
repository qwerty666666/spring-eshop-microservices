package com.example.eshop.core.catalog.domain;

import com.example.eshop.core.shared.vo.Money;
import com.example.eshop.core.shared.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Stock Keeping Unit
 */
@javax.persistence.Entity
@Table(name = "sku")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Sku implements Entity<UUID> {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter(AccessLevel.NONE)
    private UUID id;

    @NaturalId
    @Column(name = "ean", length = 13, unique = true, nullable = false, updatable = false)
    @NotNull
    @Length(min = 13, max = 13)
    private String ean;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency")),
    })
    private Money price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Override
    public UUID id() {
        return id;
    }
}
