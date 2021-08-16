package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;

public interface UpdateCustomerService {
    /**
     * Updates customer
     *
     * @throws CustomerNotFoundException if customer with provided ID not found
     * @throws EmailAlreadyExistException if new email address already in use by another customer
     */
    void updateCustomer(UpdateCustomerCommand command);
}
