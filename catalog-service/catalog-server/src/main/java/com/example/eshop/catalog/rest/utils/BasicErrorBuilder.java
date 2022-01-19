package com.example.eshop.catalog.rest.utils;

import com.example.eshop.catalog.client.api.model.BasicError;
import org.springframework.http.HttpStatus;

public class BasicErrorBuilder {
    private HttpStatus status;
    private String detail;

    private BasicErrorBuilder() {
    }

    public static BasicErrorBuilder newInstance() {
        return new BasicErrorBuilder();
    }

    public BasicErrorBuilder setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public BasicErrorBuilder setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public BasicError build() {
        var error = new BasicError();

        error.setStatus(status.value());
        error.setDetail(detail);

        return error;
    }
}
