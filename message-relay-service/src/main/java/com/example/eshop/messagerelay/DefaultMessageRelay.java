package com.example.eshop.messagerelay;

import com.example.eshop.messagerelay.OutboxProperties.DataSourceProperties;
import com.example.eshop.transactionaloutbox.messagerelay.MessageRelay;
import com.example.eshop.transactionaloutbox.messagerelay.SingleThreadedMessageRelay;
import com.example.eshop.transactionaloutbox.springdata.JdbcTemplateTransactionalOutbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.kafka.core.KafkaTemplate;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultMessageRelay implements MessageRelay {
    private static final int POLL_BATCH_SIZE = 5;
    private static final int POLL_PERIOD = 5;
    private static final TimeUnit POLL_PERIOD_TIME_UNIT = TimeUnit.SECONDS;

    private final String name;
    private final MessageRelay delegate;

    public DefaultMessageRelay(String serviceName, DataSourceProperties dataSourceProperties,
            KafkaTemplate<String, byte[]> kafkaTemplate) {
        name = serviceName;

        var dataSource = createDataSource(dataSourceProperties);

        delegate = new SingleThreadedMessageRelay(
                new JdbcTemplateTransactionalOutbox(dataSource),
                new DefaultKafkaProducer(kafkaTemplate),
                POLL_BATCH_SIZE,
                POLL_PERIOD,
                POLL_PERIOD_TIME_UNIT
        );
    }

    private DataSource createDataSource(DataSourceProperties dataSourceProperties) {
        return DataSourceBuilder.create()
                .type(dataSourceProperties.getType())
                .driverClassName(DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl()).getDriverClassName())
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .build();
    }

    @Override
    @PostConstruct
    public void start() {
        log.info("start Message Relay '" + name + "'");

        delegate.start();

        log.info("Message Relay '" + name + "' started");
    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.info("stop Message Relay '" + name + "'");

        delegate.shutdown();

        log.info("Message Relay '" + name + "' stopped");
    }
}
