CREATE TABLE transactional_outbox
(
    id            bigserial PRIMARY KEY,
    topic         character varying(255) NOT NULL,
    payload       bytea,
    aggregate     character varying(255),
    aggregate_id  character varying(255),
    type          character varying(255),
    request_id    character varying(255),
    creation_time timestamp              NOT NULL
);
