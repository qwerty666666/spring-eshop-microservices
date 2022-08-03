Service that implements [Message Relay](../libs/transactional-outbox/message-relay).

We use single Message Relay service for all microservices for simplicity.

## TODO

- [ ] dataSource type can be autodetected from jdbc url?
- [ ] is there any way to horizontal scale this service? (see note below)

## Usage

To include your microservice in the Message Relay, you should set configs for ```DataSource```
for your database in ```application.yml```

```yml
outbox:
  data-sources:
    ...
    your-service:
      url: jdbc:postgresql://localhost:5432/db
      username: username
      password: password
      type: org.postgresql.ds.PGSimpleDataSource  # DataSource implementation Class
```

Then Message Relay start polling your database and send messages to Kafka.

> **Note**: used database polling implementation is single-threaded and can't be used
> by multiple processes or concurrently.
> 
> We stick to this approach because there are two main problems with implementing concurrent 
> message queue polling:
> 1. we can't do this "really" concurrent without using locks, message duplicates 
> and in vendor-agnostic way.
> 2. generally we should provide message ordering, and therefore we should use single Kafka 
> Producer or reduce ordering requirements in any other way.