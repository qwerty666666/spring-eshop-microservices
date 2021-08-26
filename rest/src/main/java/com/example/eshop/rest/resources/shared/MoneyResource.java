package com.example.eshop.rest.resources.shared;

import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.math.BigDecimal;
import java.util.Currency;

public class MoneyResource {
    public BigDecimal amount;

    public Currency currency;

    public MoneyResource(Money money) {
        this.amount = money.getAmount();
        this.currency = money.getCurrency();
    }
}