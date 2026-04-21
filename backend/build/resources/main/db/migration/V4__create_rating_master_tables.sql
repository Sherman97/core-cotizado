CREATE TABLE postal_code_zone_map (
    postal_code VARCHAR(32) NOT NULL PRIMARY KEY,
    zone_code VARCHAR(32) NOT NULL,
    zone_name VARCHAR(120) NOT NULL,
    active BIT NOT NULL DEFAULT b'1'
);

CREATE TABLE zone_factors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    zone_code VARCHAR(32) NOT NULL,
    zone_name VARCHAR(120) NOT NULL,
    factor_value DOUBLE NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_zone_factors_zone_code UNIQUE (zone_code)
);

CREATE TABLE occupancy_catalog (
    occupancy_code VARCHAR(64) NOT NULL PRIMARY KEY,
    occupancy_name VARCHAR(255) NOT NULL,
    clave_incendio VARCHAR(64) NOT NULL,
    active BIT NOT NULL DEFAULT b'1'
);

CREATE TABLE occupancy_factors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    occupancy_code VARCHAR(64) NOT NULL,
    factor_value DOUBLE NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_occupancy_factors_code UNIQUE (occupancy_code),
    CONSTRAINT fk_occupancy_factors_catalog FOREIGN KEY (occupancy_code)
        REFERENCES occupancy_catalog (occupancy_code)
);

CREATE TABLE construction_factors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    construction_type VARCHAR(64) NOT NULL,
    factor_value DOUBLE NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_construction_factors_type UNIQUE (construction_type)
);

CREATE TABLE coverage_rate_tables (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(64) NOT NULL,
    coverage_code VARCHAR(64) NOT NULL,
    base_rate DOUBLE NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_coverage_rate_tables UNIQUE (product_code, coverage_code),
    CONSTRAINT fk_coverage_rate_tables_catalog FOREIGN KEY (coverage_code)
        REFERENCES coverage_catalog (code)
);

CREATE TABLE coverage_factor_tables (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(64) NOT NULL,
    coverage_code VARCHAR(64) NOT NULL,
    factor_value DOUBLE NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_coverage_factor_tables UNIQUE (product_code, coverage_code),
    CONSTRAINT fk_coverage_factor_tables_catalog FOREIGN KEY (coverage_code)
        REFERENCES coverage_catalog (code)
);

CREATE TABLE calculation_parameters (
    parameter_code VARCHAR(64) NOT NULL PRIMARY KEY,
    parameter_value DOUBLE NOT NULL,
    description VARCHAR(255) NULL,
    active BIT NOT NULL DEFAULT b'1'
);
