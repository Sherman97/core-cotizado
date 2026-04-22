ALTER TABLE quotes
ADD COLUMN risk_classification VARCHAR(64) NULL,
ADD COLUMN business_type VARCHAR(64) NULL;

ALTER TABLE quote_locations
ADD COLUMN location_index INT NULL,
ADD COLUMN colony VARCHAR(120) NULL,
ADD COLUMN municipality VARCHAR(120) NULL,
ADD COLUMN construction_level INT NULL,
ADD COLUMN construction_year INT NULL,
ADD COLUMN fire_key VARCHAR(64) NULL,
ADD COLUMN catastrophic_zone BOOLEAN NULL;

CREATE TABLE quote_location_guarantees (
    location_id BIGINT NOT NULL,
    guarantee VARCHAR(255) NOT NULL,
    CONSTRAINT fk_quote_location_guarantees_location FOREIGN KEY (location_id) REFERENCES quote_locations (id) ON DELETE CASCADE
);

CREATE INDEX idx_quote_location_guarantees_location_id ON quote_location_guarantees (location_id);
