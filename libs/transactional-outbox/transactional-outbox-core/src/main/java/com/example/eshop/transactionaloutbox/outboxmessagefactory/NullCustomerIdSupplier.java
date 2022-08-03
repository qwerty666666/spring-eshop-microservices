package com.example.eshop.transactionaloutbox.outboxmessagefactory;

/**
 * Returns null as current customer ID
 */
public class NullCustomerIdSupplier implements CustomerIdSupplier {
    @Override
    public String get() {
        return null;
    }
}
