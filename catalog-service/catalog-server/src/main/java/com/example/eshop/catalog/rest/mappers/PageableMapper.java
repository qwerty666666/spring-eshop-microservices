package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.Pageable;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageableMapper {
    default <T> Pageable toPageableDto(Page<T> page) {
        var pageable = new Pageable();

        pageable.setPage(page.getNumber() + 1);
        pageable.setPerPage(page.getSize());
        pageable.setTotalPages(page.getTotalPages());
        pageable.setTotalItems((int)page.getTotalElements());

        return pageable;
    }
}
