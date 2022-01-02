Provides implementation of [TransactionalOutbox Pattern](../transactional-outbox-core) based on `JdbcTemplate`.

Therefore, you can safely use this implementation within `@Transactional` methods,
and messages will be inserted in the same transaction as the called side use.

```java
@Bean
public TransactionalOutbox(DataSource dataSource){
    return new JdbcTemplateTransactionalOutbox(dataSource);
}

@Service
class MyService {
    @Autowired
    TransactionalOutbox transactionalOutbox;

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

