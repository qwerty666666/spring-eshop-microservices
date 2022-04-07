Provides implementation of [TransactionalOutbox Pattern](../transactional-outbox-core) 
based on Spring's functionality.

### Jdbc 

`JdbcTemplateTransactionalOutbox` is `TransactionalOutbox` build on top of `JdbcTemplate`.

Therefore, you can safely use this implementation within `@Transactional` methods,
and messages will be inserted in the same transaction as the called side use.

```java
@Bean
public TransactionalOutbox(DataSource dataSource){
    return new JdbcTemplateTransactionalOutbox(dataSource);
}

@Service
@RequiredArgsContructor
class MyService {
    private final TransactionalOutbox transactionalOutbox;

    @Transactional
    void doSmth() {
        ...
        
        var message = OutboxMessage.builder()
                .topic("my_topic")
                .payload("message_body".toBytes(StandardCharsets.UTF_8))
                .build();

        transactionalOutbox.add(message);
    }
}
```

### Sleuth

The lib provides `SleuthB3RequestIdSupplier` - the `RequestIdSupplier` which takes current 
request ID from Spring's Sleuth `Tracer`. The returned value is generated in 
[b3 single format](https://github.com/openzipkin/b3-propagation#single-header}) and can be 
used directly in http header for example.

```java
import org.springframework.stereotype.Service;

@Bean
public DomainEventOutboxMessageFactory outboxMessageFactory(ObjectMapper objectMapper,Tracer tracer){
    return new DomainEventOutboxMessageFactory(
        new JacksonEventSerializer(objectMapper),
        new SleuthB3RequestIdSupplier(tracer)
    );
}

@Service
@RequiredArgsContructor
class MyService {
    private final DomainEventOutboxMessageFactory messageFactory;
    
    void doSmth() {
        var message = messageFactory.create("topic-name", domainEvent, aggregateRoot);
        
        var requestId = message.getRequestId(); // 80f198ee56343ba864fe8b2a57d3eff7-e457b5a2e4d86bd1-1-05e3ac9a4f6e3b90
    }
}
```