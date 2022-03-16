package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.model.PageableDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageableMapper {
    default <T> PageableDto toPageableDto(Page<T> page) {
        var pageable = new PageableDto();

        pageable.setPage(page.getNumber() + 1);
        pageable.setPerPage(page.getSize());
        pageable.setTotalPages(page.getTotalPages());
        pageable.setTotalItems((int)page.getTotalElements());

        return pageable;
    }
}
