CREATE TABLE IF NOT EXISTS messaging_outbox (
    id bigserial PRIMARY KEY,
    aggregate_id bigint,
    event_type varchar(64) NOT NULL,
    topic varchar(255) NOT NULL,
    event_key varchar(255),
    payload text NOT NULL,
    status varchar(16) NOT NULL,
    attempts integer NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    available_at timestamptz NOT NULL DEFAULT now(),
    sent_at timestamptz,
    last_error text
);

CREATE INDEX IF NOT EXISTS messaging_outbox_status_available_idx
    ON messaging_outbox (status, available_at, id);

CREATE INDEX IF NOT EXISTS messaging_outbox_aggregate_idx
    ON messaging_outbox (aggregate_id, created_at DESC);
