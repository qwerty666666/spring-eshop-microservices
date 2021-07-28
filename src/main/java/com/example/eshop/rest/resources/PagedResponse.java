package com.example.eshop.rest.resources;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public class PagedResponse<T> {
    public List<T> items;
    public int page;
    public int perPage;
    public int totalItems;
    public int totalPages;

    public <R> PagedResponse(Page<R> page, Function<R, T> converter) {
        this.items = page.get().map(converter).toList();
        this.page = page.getNumber() + 1;
        this.perPage = page.getSize();
        this.totalItems = (int)page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
