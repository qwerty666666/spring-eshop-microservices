package com.example.eshop.warehouse;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "kafka.disabled=true" })
public @interface ExcludeKafkaConfig {
}
