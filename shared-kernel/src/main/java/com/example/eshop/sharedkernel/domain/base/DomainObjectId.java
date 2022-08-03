package com.example.eshop.sharedkernel.domain.base;

import com.example.eshop.sharedkernel.domain.Assertions;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for value objects that are used as Entity Identifiers.
 * <p>
 * These Identifiers are usually used by {@link AggregateRoot} for semantic references
 * to other Aggregate Roots.
 */
@MappedSuperclass
public abstract class DomainObjectId implements ValueObject, Serializable {
    @Column(name = "id", nullable = false, updatable = false, insertable = false)
    protected String uuid;

    protected DomainObjectId() {
    }

    protected DomainObjectId(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Creates random instance of {@code idClass}
     *
     * @throws DomainObjectIdInstantiationException if {@code idClass} has no String constructor
     */
    public static <ID extends DomainObjectId> ID randomId(Class<ID> idClass) {
        Assertions.notNull(idClass, "idClass must not be null");

        try {
            return idClass.getConstructor(String.class).newInstance(UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new DomainObjectIdInstantiationException("Can't create " + idClass + " instance", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DomainObjectId id = (DomainObjectId) o;

        return uuid != null && Objects.equals(uuid, id.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return uuid;
    }
}
