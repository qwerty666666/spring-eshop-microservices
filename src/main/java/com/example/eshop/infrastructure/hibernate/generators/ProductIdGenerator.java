package com.example.eshop.infrastructure.hibernate.generators;

import com.example.eshop.core.catalog.domain.Product;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class ProductIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return new Product.ProductId(UUID.randomUUID());
    }
}
