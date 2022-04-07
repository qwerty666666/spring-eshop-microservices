package com.example.eshop.messagerelay;

import com.example.eshop.messagerelay.OutboxProperties.DataSourceProperties;
import com.example.eshop.transactionaloutbox.messagerelay.BrokerProducer;
import com.example.eshop.transactionaloutbox.messagerelay.MessageRelay;
import com.example.eshop.transactionaloutbox.spring.JdbcTemplateTransactionalOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * Creates message relays from {@link OutboxProperties} and runs them.
 * <p>
 * Message relays are reinited when Application Context refreshed.
 */
@Component
@RequiredArgsConstructor
public class MessageRelayRegistry {
    private final GenericApplicationContext context;
    private final OutboxProperties outboxProperties;
    private final BrokerProducer brokerProducer;

    @PostConstruct
    private void init() {
        initAndRunMessageRelays();
    }

    @EventListener
    public void onRefreshScopeRefreshedEvent(EnvironmentChangeEvent event) {
        if (shouldReinitMessageRelays(event)) {
            initAndRunMessageRelays();
        }
    }

    private boolean shouldReinitMessageRelays(EnvironmentChangeEvent event) {
        return event.getKeys().stream()
                .anyMatch(configName -> configName.startsWith("outbox."));
    }

    /**
     * Creates message relays from {@link OutboxProperties}
     * and runs them
     */
    private void initAndRunMessageRelays() {
        outboxProperties.getDataSources().forEach((serviceName, dataSourceProperties) -> {
            // we register message relays as Spring beans to support
            // context lifecycle hooks
            var beanName = getMessageRelayBeanNameForService(serviceName);

            // remove message relay from context for the case if context
            // is refreshed
            deregisterMessageRelay(beanName);

            // register message relay bean and run it
            context.registerBean(beanName, MessageRelay.class,
                    () -> createMessageRelay(serviceName, dataSourceProperties));
            context.getBean(beanName, MessageRelay.class).start();
        });
    }

    private String getMessageRelayBeanNameForService(String serviceName) {
        return serviceName + "_MessageRelay";
    }

    /**
     * Stops the message relay and remove it from application context
     */
    private void deregisterMessageRelay(String beanName) {
        try {
            context.removeBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            // if bean does not exist then ignore it
        }
    }

    private MessageRelay createMessageRelay(String serviceName, DataSourceProperties dataSourceProperties) {
        var transactionalOutbox = new JdbcTemplateTransactionalOutbox(dataSourceProperties.createDataSource());

        return new DefaultMessageRelay(serviceName, transactionalOutbox, brokerProducer);
    }
}
