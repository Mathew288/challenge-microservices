-- Outbox + Processed Message (idempotencia)
-- Nota: usamos UUID como PK (PostgreSQL). En H2 (tests) funciona con MODE=PostgreSQL.

create table if not exists outbox_message (
    id uuid primary key,
    aggregate_type varchar(100) not null,
    aggregate_id uuid not null,
    message_type varchar(150) not null,
    routing_key varchar(200) not null,
    exchange varchar(200) not null,
    payload jsonb not null,
    status varchar(30) not null,
    created_at timestamptz not null,
    published_at timestamptz null,
    attempts int not null default 0,
    last_error text null
);

create index if not exists idx_outbox_message_status_created_at
    on outbox_message(status, created_at);

create table if not exists processed_message (
    id uuid primary key,
    message_id uuid not null,
    consumer varchar(200) not null,
    processed_at timestamptz not null
);

create unique index if not exists uq_processed_message_consumer_message_id
    on processed_message(consumer, message_id);
