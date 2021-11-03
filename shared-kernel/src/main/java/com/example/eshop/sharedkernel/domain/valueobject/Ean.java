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
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ean implements ValueObject {
    private static final Pattern EAN_PATTERN = Pattern.compile("^[0-9]{13}$");

    @Column(name = "ean")
    @EAN
    private String eanCode;

    private Ean(String eanCode) {
        Assertions.notNull(eanCode, "EAN must be non null");

        if (!EAN_PATTERN.matcher(eanCode).matches()) {
            throw new InvalidEanFormatException(eanCode, "Invalid EAN format. Expected EAN-13, but provided " + eanCode);
        }

        this.eanCode = eanCode;
    }

    /**
     * Creates EAN from given String
     *
     * @param ean EAN-13
     *
     * @throws InvalidEanFormatException if ean has invalid format
     */
    public static Ean fromString(String ean) {
        return new Ean(ean);
    }

    @Override
    public String toString() {
        return eanCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ean email1 = (Ean) o;

        return Objects.equals(eanCode, email1.eanCode);
    }

    @Override
    public int hashCode() {
        return eanCode.hashCode();
    }
}
