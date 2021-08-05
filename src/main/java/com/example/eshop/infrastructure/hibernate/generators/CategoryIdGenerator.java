package com.example.eshop.infrastructure.hibernate.generators;

import com.example.eshop.core.catalog.domain.Category;
import com.example.eshop.core.catalog.domain.Category.CategoryId;
import com.example.eshop.core.catalog.domain.Product;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.util.UUID;

public class CategoryIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return new CategoryId(UUID.randomUUID());
    }
}
