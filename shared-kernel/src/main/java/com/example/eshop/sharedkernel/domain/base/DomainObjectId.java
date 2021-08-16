package com.example.eshop.sharedkernel.domain.base;

import com.example.eshop.sharedkernel.domain.Assertions;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

/**
 * Base class for value objects that are used as Entity Identifiers.
 * <p>
 * This Identifiers are usually used by {@link AggregateRoot} for semantic references
 * to other Aggregate Roots.
 */
@MappedSuperclass
public abstract class DomainObjectId implements ValueObject, Serializable {
    @Column(name = "id", nullable = false)
    protected String uuid;

    protected DomainObjectId(String uuid) {
        this.uuid = uuid;
    }

    protected DomainObjectId() {
    }

    /**
     * Creates random instance of {@code idClass}
     *
     * @throws RuntimeException if {@code idClass} has no String constructor
     */
    public static <ID extends DomainObjectId> ID randomId(Class<ID> idClass) {
        Assertions.notNull(idClass, "idClass must not be null");

        try {
            return idClass.getConstructor(String.class).newInstance(UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new RuntimeException("Can't create " + idClass + " instance", e);
        }
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DomainObjectId that = (DomainObjectId) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public String toString() {
        return uuid;
    }
}
