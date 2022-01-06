package com.example.eshop.messagerelay;

import com.example.eshop.transactionaloutbox.messagerelay.MessageRelay;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
})
@EnableConfigurationProperties(OutboxProperties.class)
public class MessageRelayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageRelayServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner runMessageRelays(GenericApplicationContext context, OutboxProperties outboxProperties,
            KafkaTemplate<String, byte[]> kafkaTemplate) {
        return args -> outboxProperties.getDataSources().forEach((serviceName, dataSourceProperties) -> {
                // we register message relays as Spring beans to support
                // context lifecycle hooks
                var beanName = serviceName + "_MessageRelay";

                context.registerBean(beanName, MessageRelay.class,
                        () -> new DefaultMessageRelay(serviceName, dataSourceProperties, kafkaTemplate));

                // create bean and run it
                context.getBean("warehouse_MessageRelay", MessageRelay.class).start();
        });
    }
}
