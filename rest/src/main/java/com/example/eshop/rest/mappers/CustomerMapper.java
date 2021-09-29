package com.example.eshop.rest.mappers;

import com.example.eshop.customer.application.signup.SignUpCommand;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.rest.dto.CustomerDto;
import com.example.eshop.rest.dto.NewCustomerDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = EmailMapper.class)
public interface CustomerMapper {
    CustomerDto toCustomerDto(Customer customer);

    default String toString(CustomerId id) {
        return id == null ? null : id.toString();
    }

    SignUpCommand toSignUpCommand(NewCustomerDto dto);
}
