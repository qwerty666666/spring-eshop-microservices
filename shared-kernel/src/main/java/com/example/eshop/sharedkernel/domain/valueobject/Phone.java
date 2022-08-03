package com.example.eshop.sharedkernel.domain.valueobject;

import com.example.eshop.sharedkernel.domain.base.ValueObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phone implements ValueObject {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");

    @Column(name = "phone")
    private String phone;

    /**
     * @throws InvalidPhoneFormatException if {@code phone} is null or bad formed
     */
    private Phone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneFormatException(phone, "Invalid phone format. Expected +7xxxxxxxxxx, but provided " +
                    phone);
        }
        this.phone = phone;
    }

    /**
     * @param phone phone number in format +7xxxxxxxxxx
     *
     * @throws InvalidPhoneFormatException if {@code phone} is null or bad formed
     */
    @JsonCreator
    public static Phone fromString(String phone) {
        return new Phone(phone);
    }

    @Override
    @JsonValue
    public String toString() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Phone other = (Phone) o;

        return Objects.equals(phone, other.phone);
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }
}
