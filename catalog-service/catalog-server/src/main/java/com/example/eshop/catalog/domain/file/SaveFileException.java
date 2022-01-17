package com.example.eshop.catalog.domain.file;

public class SaveFileException extends RuntimeException {
    public SaveFileException(String message) {
        super(message);
    }

    public SaveFileException(Throwable cause) {
        super(cause);
    }
}
