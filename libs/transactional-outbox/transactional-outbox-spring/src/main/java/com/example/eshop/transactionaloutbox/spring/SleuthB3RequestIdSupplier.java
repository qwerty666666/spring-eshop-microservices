package com.example.eshop.transactionaloutbox.spring;

import brave.propagation.B3SingleFormat;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.RequestIdSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.brave.bridge.BraveTraceContext;

/**
 * Supply request ID from {@link Tracer} context.
 * <p>
 * The returned value is in b3 single format
 * {@see https://github.com/openzipkin/b3-propagation#single-header}
 */
@RequiredArgsConstructor
public class SleuthB3RequestIdSupplier implements RequestIdSupplier {
    private final Tracer tracer;

    @Override
    public String get() {
        var currentSpan = tracer.currentSpan();

        if (currentSpan == null) {
            return "";
        }

        return B3SingleFormat.writeB3SingleFormat(BraveTraceContext.toBrave(currentSpan.context()));
    }
}
