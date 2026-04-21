CREATE TABLE geo_departments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(32) NOT NULL,
    department_name VARCHAR(120) NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_geo_departments_code UNIQUE (department_code)
);

CREATE TABLE geo_cities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    city_code VARCHAR(32) NOT NULL,
    city_name VARCHAR(120) NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT uq_geo_cities_dept_city UNIQUE (department_id, city_code),
    CONSTRAINT fk_geo_cities_department FOREIGN KEY (department_id) REFERENCES geo_departments (id)
);

CREATE TABLE geo_postal_codes (
    postal_code VARCHAR(32) NOT NULL PRIMARY KEY,
    city_id BIGINT NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    CONSTRAINT fk_geo_postal_codes_city FOREIGN KEY (city_id) REFERENCES geo_cities (id)
);

CREATE INDEX idx_geo_cities_department_id ON geo_cities (department_id);
CREATE INDEX idx_geo_postal_codes_city_id ON geo_postal_codes (city_id);
