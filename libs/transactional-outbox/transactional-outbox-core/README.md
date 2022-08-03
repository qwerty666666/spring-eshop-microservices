Implementation of the [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html).

## Usage

### Getting Started

#### 1. Setup Database

Your project should have database with the following schema (Postgres syntax):

```sql
CREATE TABLE transactional_outbox
(
    id            bigserial PRIMARY KEY,           
    topic         character varying(255) NOT NULL,  -- message topic
    payload       bytea,                            -- message body
    key           character varying(255),           -- message key
    aggregate     character varying(255),           -- source Aggregate class FQN
    aggregate_id  character varying(255),           -- source Aggregate ID
    type          character varying(255),           -- message class FQN
    request_id    character varying(255),           -- request ID for distributed tracing
    customer_id   character varying(255),           -- customer ID for distributed tracing
    creation_time timestamp              NOT NULL   -- UTC time when OutboxMessage was created
);
```

#### 2. Create TransactionalOutbox instance

You can use [JdbcTemplateTransactionalOutbox](../transactional-outbox-spring) if you
use Spring's transaction management.

```java
TransactionalOutbox transactionalOutbox = new JdbcTemplateTransactionalOutbox(dataSource);
```

Or implement it by yourself. 
> _Note_: `TransactionalOutbox` should save messages in callers transaction boundaries, i.e. use
> the same connection as the caller side do.

#### 3. Save messages to outbox

You should save messages to outbox within transaction.

```java
@Transactional
void doSmth() {
    var message = OutboxMessage.builder()
        .topic("my_topic")
        .payload("message_body".toBytes(StandardCharsets.UTF_8))
        .build();

    transactionalOutbox.add(message);
}
```

### Saving DDD DomainEvents

To create `OutboxMessages` from `AggregateRoot` events you can use special factory,
which will fill aggregate source fields by itself.

```java
AggregateRoot sourceAggregate = ...;
DomainEvent event = ...;

var messageFactory = new DomainEventOutboxMessageFactory(
        event -> toJson(event),        // DomainEvent serializer
        () -> getCurrentRequestId()    // Request ID Supplier
);

messageFactory.create("my_topic", event, soruceAggregate);
```

