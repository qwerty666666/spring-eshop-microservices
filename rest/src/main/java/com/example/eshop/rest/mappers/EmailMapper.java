package com.example.eshop.rest.mappers;

import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {
    default String toString(Email email) {
        return email.toString();
    }

    default Email toEmail(String email) {
        return Email.fromString(email);
    }
}
