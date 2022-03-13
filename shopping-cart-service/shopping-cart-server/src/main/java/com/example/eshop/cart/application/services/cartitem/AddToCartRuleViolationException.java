package com.example.eshop.cart.application.services.cartitem;

/**
 * Exception is thrown when item can't be added to the cart.
 *
 * The reason why item can't be added should be saved to
 * {@link AddToCartRuleViolationException#getCause()}.
 */
public class AddToCartRuleViolationException extends RuntimeException {
    public AddToCartRuleViolationException(Throwable cause) {
        super(cause);
    }
}
