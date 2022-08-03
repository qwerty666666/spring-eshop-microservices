package com.example.eshop.springdatajpautils;

import org.hibernate.Session;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import java.io.Serializable;
import java.util.Optional;

/**
 * Support for Hibernate's {@link Session#bySimpleNaturalId(Class)}
 */
@NoRepositoryBean
public interface SimpleNaturalIdRepository<T, ID, NID extends Serializable> extends JpaRepository<T, ID> {
    /**
     * Finds Entity by Hibernate's {@link NaturalId}
     */
    Optional<T> findByNaturalId(NID naturalId);
}
