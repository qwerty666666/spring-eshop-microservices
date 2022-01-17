package com.example.eshop.transactionaloutbox.springdata;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * {@link TransactionalOutbox} implementation build on top of Spring's
 * {@link JdbcTemplate}. Therefore, you can use it as singleton Bean within
 * {@link Transactional} methods, because of {@link JdbcTemplate} will use
 * connection which hold current transaction.
 * <p>
 * This class is not support concurrent message handling.
 * Concurrent message polling will cause message duplication on
 * producer side.
 */
public class JdbcTemplateTransactionalOutbox implements TransactionalOutbox {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcTemplateTransactionalOutbox(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource is required");

        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void add(List<OutboxMessage> messages) {
        jdbcTemplate.batchUpdate(
                """
                INSERT INTO transactional_outbox(aggregate, aggregate_id, type, topic, payload, request_id, creation_time)
                VALUES (:aggregate, :aggregate_id, :type, :topic, :payload, :request_id, :creation_time)""",
                messages.stream()
                        .map(message -> new MapSqlParameterSource()
                                .addValue("aggregate", message.getAggregate())
                                .addValue("aggregate_id", message.getAggregateId())
                                .addValue("type", message.getType())
                                .addValue("topic", message.getTopic())
                                .addValue("payload", message.getPayload())
                                .addValue("request_id", message.getRequestId())
                                .addValue("creation_time", Timestamp.from(message.getCreationTime()))
                        )
                        .toArray(SqlParameterSource[]::new)
        );
    }

    @Override
    public List<OutboxMessage> getMessages(int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM transactional_outbox ORDER BY id LIMIT :limit",
                new MapSqlParameterSource("limit", limit),
                (rs, rowNum) -> createOutboxMessage(rs)
        );
    }

    @SneakyThrows
    private OutboxMessage createOutboxMessage(ResultSet rs) {
        return new OutboxMessage(
                rs.getInt("id"),
                rs.getString("aggregate"),
                rs.getString("aggregate_id"),
                rs.getString("type"),
                rs.getString("topic"),
                rs.getString("key"),
                rs.getBytes("payload"),
                rs.getString("request_id"),
                rs.getTimestamp("creation_time").toInstant()
        );
    }

    @Override
    public void remove(List<OutboxMessage> messages) {
        if (messages.isEmpty()) {
            return;
        }

        jdbcTemplate.update(
                "DELETE FROM transactional_outbox WHERE id in (:ids)",
                new MapSqlParameterSource("ids", messages.stream().map(OutboxMessage::getId).toList())
        );
    }
}
