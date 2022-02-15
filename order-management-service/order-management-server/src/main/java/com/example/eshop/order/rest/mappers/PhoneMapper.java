package com.example.eshop.order.rest.mappers;

import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.mapstruct.Mapper;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring")
public interface PhoneMapper {
    @Nullable
    default String toString(@Nullable Phone phone) {
        return phone == null ? null : phone.toString();
    }

    @Nullable
    default Phone toPhone(@Nullable String phone) {
        return phone == null ? null : Phone.fromString(phone);
    }
}
