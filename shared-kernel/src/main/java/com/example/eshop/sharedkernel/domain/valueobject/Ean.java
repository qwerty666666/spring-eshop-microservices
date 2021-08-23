package com.example.eshop.sharedkernel.domain.valueobject;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.EAN;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ean implements ValueObject {
    @Column(name = "ean")
    @EAN
    private String ean;

    private Ean(String ean) {
        this.ean = ean;
    }

    /**
     * Creates EAN from given String
     *
     * @throws IllegalArgumentException if ean has invalid format
     */
    public static Ean fromString(String ean) {
        Assertions.ean(ean, "Invalid EAN");
        return new Ean(ean);
    }

    @Override
    public String toString() {
        return ean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ean email1 = (Ean) o;

        return Objects.equals(ean, email1.ean);
    }

    @Override
    public int hashCode() {
        return ean.hashCode();
    }
}
