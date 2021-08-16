package com.example.eshop.customer.domain.customer;

import com.example.eshop.sharedkernel.domain.base.ValueObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

/**
 * Hashed password for storing in Databases
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashedPassword implements ValueObject {
    @NotEmpty
    private String hashedPassword;

    private HashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public static HashedPassword fromHash(String hashedPassword) {
        return new HashedPassword(hashedPassword);
    }

    @Override
    public String toString() {
        return hashedPassword;
    }
}
