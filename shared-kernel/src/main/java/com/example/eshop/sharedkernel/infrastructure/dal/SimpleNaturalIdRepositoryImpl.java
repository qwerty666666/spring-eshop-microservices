package com.example.eshop.sharedkernel.infrastructure.dal;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;

public class SimpleNaturalIdRepositoryImpl<T, ID, NID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements SimpleNaturalIdRepository<T, ID, NID> {
    private final EntityManager em;

    public SimpleNaturalIdRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.em = entityManager;
    }

    @Override
    public Optional<T> findByNaturalId(NID naturalId) {
        return em.unwrap(Session.class)
                .bySimpleNaturalId(this.getDomainClass())
                .loadOptional(naturalId);
    }
}
