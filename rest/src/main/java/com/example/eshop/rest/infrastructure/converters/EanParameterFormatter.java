package com.example.eshop.rest.infrastructure.converters;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.InvalidEanFormatException;
import org.springframework.format.Formatter;
import java.util.Locale;

public class EanParameterFormatter implements Formatter<Ean> {
    @Override
    public Ean parse(String ean, Locale locale) {
        try {
            return Ean.fromString(ean);
        } catch (InvalidEanFormatException e) {
            // throw our own exception to handle it in @ExceptionHandlers
            // (and ignore InvalidEanFormatException exceptions thrown by
            // application)
            throw new EanParameterInvalidFormatException(e.getEan());
        }
    }

    @Override
    public String print(Ean ean, Locale locale) {
        return ean.toString();
    }
}
