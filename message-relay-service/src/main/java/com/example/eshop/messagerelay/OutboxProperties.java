package com.example.eshop.messagerelay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("outbox")
public class OutboxProperties {
    private Map<String, DataSourceProperties> dataSources = new HashMap<>();

    @Getter
    @Setter
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
        private Class<? extends DataSource> type;
    }
}
