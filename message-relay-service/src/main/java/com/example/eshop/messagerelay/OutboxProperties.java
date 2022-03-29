package com.example.eshop.messagerelay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("outbox")
public class OutboxProperties {
    /**
     * Data sources from which messages will be polled
     */
    private Map<String, DataSourceProperties> dataSources = new HashMap<>();

    @Getter
    @Setter
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
        private Class<? extends DataSource> type;

        public DataSource createDataSource() {
            return DataSourceBuilder.create()
                    .type(type)
                    .driverClassName(DatabaseDriver.fromJdbcUrl(url).getDriverClassName())
                    .url(url)
                    .username(username)
                    .password(password)
                    .build();
        }
    }
}
