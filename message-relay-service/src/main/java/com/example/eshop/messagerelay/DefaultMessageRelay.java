package com.example.eshop.messagerelay;

import com.example.eshop.transactionaloutbox.messagerelay.MessageRelay;
import com.example.eshop.transactionaloutbox.messagerelay.SingleThreadedMessageRelay;
import com.example.eshop.transactionaloutbox.springdata.JdbcTemplateTransactionalOutbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultMessageRelay implements MessageRelay {
    private static final int DEFAULT_POLL_BATCH_SIZE = 5;
    private static final int DEFAULT_POLL_PERIOD = 5;
    private static final TimeUnit DEFAULT_POLL_PERIOD_TIME_UNIT = TimeUnit.SECONDS;

    private final MessageRelay delegate;

    public DefaultMessageRelay(String serviceName, DataSource dataSource, KafkaTemplate<String, byte[]> kafkaTemplate) {
        this(serviceName, dataSource, kafkaTemplate, DEFAULT_POLL_BATCH_SIZE, DEFAULT_POLL_PERIOD,
                DEFAULT_POLL_PERIOD_TIME_UNIT);
    }

    public DefaultMessageRelay(String serviceName, DataSource dataSource, KafkaTemplate<String, byte[]> kafkaTemplate,
            int pollBatchSize, int pollPeriod, TimeUnit pollTimeUnit) {
        delegate = new SingleThreadedMessageRelay(
                serviceName,
                new JdbcTemplateTransactionalOutbox(dataSource),
                new DefaultKafkaProducer(kafkaTemplate),
                pollBatchSize,
                pollPeriod,
                pollTimeUnit
        );
    }

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    @PreDestroy
    public void shutdown() {
        delegate.shutdown();
    }
}
