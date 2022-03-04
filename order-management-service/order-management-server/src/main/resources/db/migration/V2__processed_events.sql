CREATE TABLE IF NOT EXISTS processed_messages
(
    id            bigint                 NOT NULL,
    message_key   character varying(255) NOT NULL,
    message_class character varying(255),
    CONSTRAINT processed_messages_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS processed_message_uniq_idx ON processed_messages (message_key, message_class);