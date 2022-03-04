package com.example.eshop.order.infrastructure.messaging;

import com.example.eshop.sharedkernel.domain.Assertions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Message Broker message that was processed before
 */
@Entity
@Table(
        name = "processed_messages",
        indexes = @Index(name = "processed_message_uniq_idx", columnList = "message_key, message_class", unique = true)
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProcessedMessage {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "message_key", nullable = false)
    @NotNull
    private String messageKey;

    @Column(name = "message_class", nullable = false)
    private Class<?> messageClass;

    public ProcessedMessage(String messageKey, Class<?> messageClass) {
        Assertions.notEmpty(messageKey, "messageKey is required");

        this.messageKey = messageKey;
        this.messageClass = messageClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProcessedMessage that = (ProcessedMessage) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
