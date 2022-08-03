package com.example.eshop.transactionaloutbox.outboxmessagefactory;

/**
 * Returns null as current request ID
 */
public class NullRequestIdSupplier implements RequestIdSupplier {
    @Override
    public String get() {
        return null;
    }
}
