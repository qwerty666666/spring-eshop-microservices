package com.example.eshop.customer.domain.customer;

/**
 * Factory for create {@link HashedPassword} and ensure its policies
 */
public interface HashedPasswordFactory {
    /**
     * @throws PasswordPolicyException if plain password does not comply to all policies
     */
    HashedPassword createFromPlainPassword(String plain);
}
