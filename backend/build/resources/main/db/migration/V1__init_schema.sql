CREATE SEQUENCE quote_location_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE quotes (
    folio VARCHAR(64) NOT NULL PRIMARY KEY,
    root_folio VARCHAR(64) NOT NULL,
    parent_quote_folio VARCHAR(64) NULL,
    product_code VARCHAR(64) NULL,
    customer_name VARCHAR(255) NULL,
    currency VARCHAR(16) NULL,
    observations VARCHAR(1000) NULL,
    layout_expected_location_count INT NULL,
    layout_capture_risk_zone BIT NULL,
    layout_capture_georeference BIT NULL,
    layout_notes VARCHAR(1000) NULL,
    status VARCHAR(32) NOT NULL,
    business_version INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    lock_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_quotes_root_version UNIQUE (root_folio, business_version)
);

CREATE INDEX idx_quotes_root_folio ON quotes (root_folio);

CREATE TABLE quote_locations (
    id BIGINT NOT NULL PRIMARY KEY,
    quote_folio VARCHAR(64) NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    city VARCHAR(120) NULL,
    department VARCHAR(120) NULL,
    address VARCHAR(255) NULL,
    postal_code VARCHAR(32) NULL,
    construction_type VARCHAR(64) NULL,
    occupancy_type VARCHAR(64) NULL,
    insured_value BIGINT NOT NULL,
    validation_status VARCHAR(32) NOT NULL,
    CONSTRAINT fk_quote_locations_quote FOREIGN KEY (quote_folio) REFERENCES quotes (folio)
);

CREATE INDEX idx_quote_locations_quote_folio ON quote_locations (quote_folio);

CREATE TABLE quote_location_alerts (
    location_id BIGINT NOT NULL,
    alert_order INT NOT NULL,
    alert_message VARCHAR(255) NOT NULL,
    PRIMARY KEY (location_id, alert_order),
    CONSTRAINT fk_quote_location_alerts_location FOREIGN KEY (location_id) REFERENCES quote_locations (id)
);

CREATE TABLE quote_coverages (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quote_folio VARCHAR(64) NOT NULL,
    coverage_code VARCHAR(64) NOT NULL,
    coverage_name VARCHAR(255) NOT NULL,
    insured_limit BIGINT NOT NULL,
    deductible_type VARCHAR(64) NULL,
    deductible_value BIGINT NULL,
    selected BIT NOT NULL,
    CONSTRAINT fk_quote_coverages_quote FOREIGN KEY (quote_folio) REFERENCES quotes (folio),
    CONSTRAINT uq_quote_coverages_quote_code UNIQUE (quote_folio, coverage_code)
);

CREATE INDEX idx_quote_coverages_quote_folio ON quote_coverages (quote_folio);

CREATE TABLE coverage_catalog (
    code VARCHAR(64) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BIT NOT NULL
);

CREATE TABLE quote_calculation_results (
    quote_folio VARCHAR(64) NOT NULL PRIMARY KEY,
    net_premium DOUBLE NOT NULL,
    expense_amount DOUBLE NOT NULL,
    tax_amount DOUBLE NOT NULL,
    total_premium DOUBLE NOT NULL,
    lock_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_quote_calculation_results_quote FOREIGN KEY (quote_folio) REFERENCES quotes (folio)
);

CREATE TABLE quote_calculation_alerts (
    quote_folio VARCHAR(64) NOT NULL,
    alert_order INT NOT NULL,
    alert_message VARCHAR(255) NOT NULL,
    PRIMARY KEY (quote_folio, alert_order),
    CONSTRAINT fk_quote_calculation_alerts_result FOREIGN KEY (quote_folio)
        REFERENCES quote_calculation_results (quote_folio)
);

CREATE TABLE location_calculation_results (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quote_folio VARCHAR(64) NOT NULL,
    location_id BIGINT NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    premium DOUBLE NOT NULL,
    CONSTRAINT fk_location_calculation_results_result FOREIGN KEY (quote_folio)
        REFERENCES quote_calculation_results (quote_folio)
);

CREATE INDEX idx_location_calculation_results_quote_folio
    ON location_calculation_results (quote_folio);

CREATE TABLE location_calculation_alerts (
    location_result_id BIGINT NOT NULL,
    alert_order INT NOT NULL,
    alert_message VARCHAR(255) NOT NULL,
    PRIMARY KEY (location_result_id, alert_order),
    CONSTRAINT fk_location_calculation_alerts_result FOREIGN KEY (location_result_id)
        REFERENCES location_calculation_results (id)
);

CREATE TABLE calculation_traces (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quote_folio VARCHAR(64) NOT NULL,
    location_id BIGINT NULL,
    factor_type VARCHAR(64) NOT NULL,
    applied_value DOUBLE NOT NULL,
    factor_order INT NOT NULL,
    CONSTRAINT fk_calculation_traces_quote FOREIGN KEY (quote_folio) REFERENCES quotes (folio)
);

CREATE INDEX idx_calculation_traces_quote_folio ON calculation_traces (quote_folio);

CREATE TABLE calculation_trace_metadata (
    trace_id BIGINT NOT NULL,
    metadata_key VARCHAR(64) NOT NULL,
    metadata_value VARCHAR(255) NULL,
    PRIMARY KEY (trace_id, metadata_key),
    CONSTRAINT fk_calculation_trace_metadata_trace FOREIGN KEY (trace_id) REFERENCES calculation_traces (id)
);
