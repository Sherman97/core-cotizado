CREATE TABLE folio_idempotency_keys (
    idempotency_key VARCHAR(128) NOT NULL PRIMARY KEY,
    folio VARCHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_folio_idempotency_quote FOREIGN KEY (folio) REFERENCES quotes (folio)
);
