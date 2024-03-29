package com.example.eshop.transactionaloutbox.spring;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
class JdbcTemplateTransactionalOutboxTest {
    private static final Integer ID = 1;
    private static final String TOPIC = "topic";
    private static final byte[] PAYLOAD = new byte[]{};
    private static final String KEY = "key";
    private static final String AGGREGATE = "java.lang.Object";
    private static final String AGGREGATE_ID = "aggregateId";
    private static final String REQUEST_ID = "requestId";
    private static final String CUSTOMER_ID = "customerId";
    private static final String TYPE = "java.lang.Object";
    private static final Instant CREATION_TIME = LocalDate.parse("2016-04-17").atStartOfDay().toInstant(ZoneOffset.UTC);
    private static final OutboxMessage MESSAGE = new OutboxMessage(ID, AGGREGATE, AGGREGATE_ID, TYPE, TOPIC, KEY, PAYLOAD,
            REQUEST_ID, CUSTOMER_ID, CREATION_TIME);

    private JdbcDataSource dataSource;
    private TransactionalOutbox transactionalOutbox;

    @BeforeAll
    void beforeAll() throws SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        try (var stmt = dataSource.getConnection().createStatement()) {
            stmt.execute("""
                    create table transactional_outbox(
                        id            bigint auto_increment,
                        aggregate     varchar(255),
                        aggregate_id  varchar(255),
                        type          varchar(255),
                        topic         varchar(255) not null,
                        payload       bytea,
                        key           bytea,
                        request_id    varchar(255),
                        customer_id   varchar(255),
                        creation_time timestamp not null
                    )"""
            );
        }

        transactionalOutbox = new JdbcTemplateTransactionalOutbox(dataSource);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var stmt = dataSource.getConnection().createStatement()) {
            stmt.execute("truncate table transactional_outbox");
        }
    }

    @Test
    void testAdd() throws SQLException {
        // When
        transactionalOutbox.add(List.of(MESSAGE));

        // Then
        try (var stmt = dataSource.getConnection().createStatement();
             var rs = stmt.executeQuery("select * from transactional_outbox");
        ) {
            assertTrue(rs.next());

            assertEquals(MESSAGE.getId(), rs.getInt("id"));
            assertEquals(MESSAGE.getTopic(), rs.getString("topic"));
            assertArrayEquals(MESSAGE.getPayload(), rs.getBytes("payload"));
            assertEquals(MESSAGE.getAggregate(), rs.getString("aggregate"));
            assertEquals(MESSAGE.getAggregateId(), rs.getString("aggregate_id"));
            assertEquals(MESSAGE.getRequestId(), rs.getString("request_id"));
            assertEquals(MESSAGE.getCustomerId(), rs.getString("customer_id"));
            assertEquals(MESSAGE.getType(), rs.getString("type"));
            assertEquals(MESSAGE.getCreationTime(), rs.getTimestamp("creation_time").toInstant());

            assertFalse(rs.next());
        }
    }

    @Test
    void testGetMessages() throws SQLException {
        fillTestData();

        var messages = transactionalOutbox.getMessages(1);

        assertEquals(1, messages.size());
        assertEquals(TOPIC, messages.get(0).getTopic());
        assertArrayEquals(PAYLOAD, messages.get(0).getPayload());
        assertEquals(AGGREGATE, messages.get(0).getAggregate());
        assertEquals(AGGREGATE_ID, messages.get(0).getAggregateId());
        assertEquals(REQUEST_ID, messages.get(0).getRequestId());
        assertEquals(CUSTOMER_ID, messages.get(0).getCustomerId());
        assertEquals(TYPE, messages.get(0).getType());
        assertEquals(CREATION_TIME, messages.get(0).getCreationTime());
    }

    @Test
    void testRemove() throws SQLException {
        fillTestData();

        transactionalOutbox.remove(MESSAGE);

        try (var stmt = dataSource.getConnection().createStatement();
             var rs = stmt.executeQuery("select * from transactional_outbox");
        ) {
            assertFalse(rs.next());
        }
    }

    private void fillTestData() throws SQLException {
        try (var ps = dataSource.getConnection().prepareStatement("""
                insert into transactional_outbox(id, aggregate, aggregate_id, type, topic, payload, request_id,
                   customer_id, creation_time)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)""")) {
            ps.setInt(1, ID);
            ps.setString(2, AGGREGATE);
            ps.setString(3, AGGREGATE_ID);
            ps.setString(4, TYPE);
            ps.setString(5, TOPIC);
            ps.setObject(6, PAYLOAD);
            ps.setString(7, REQUEST_ID);
            ps.setString(8, CUSTOMER_ID);
            ps.setTimestamp(9, Timestamp.from(CREATION_TIME));

            ps.execute();
        }
    }
}
