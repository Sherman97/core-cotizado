INSERT INTO geo_departments (department_code, department_name, active) VALUES
('CUN', 'Cundinamarca', b'1'),
('ANT', 'Antioquia', b'1'),
('ATL', 'Atlantico', b'1');

INSERT INTO geo_cities (department_id, city_code, city_name, active)
SELECT d.id, x.city_code, x.city_name, b'1'
FROM (
    SELECT 'CUN' AS department_code, 'BOG' AS city_code, 'Bogota' AS city_name
    UNION ALL SELECT 'CUN', 'SOA', 'Soacha'
    UNION ALL SELECT 'CUN', 'CHI', 'Chia'
    UNION ALL SELECT 'ANT', 'MED', 'Medellin'
    UNION ALL SELECT 'ANT', 'ENV', 'Envigado'
    UNION ALL SELECT 'ANT', 'RIO', 'Rionegro'
    UNION ALL SELECT 'ATL', 'BAR', 'Barranquilla'
    UNION ALL SELECT 'ATL', 'SOL', 'Soledad'
    UNION ALL SELECT 'ATL', 'PUR', 'Puerto Colombia'
) x
JOIN geo_departments d ON d.department_code = x.department_code;

INSERT INTO geo_postal_codes (postal_code, city_id, active)
SELECT p.postal_code, c.id, b'1'
FROM (
    SELECT 'BOG' AS city_code, '110111' AS postal_code
    UNION ALL SELECT 'BOG', '110121'
    UNION ALL SELECT 'SOA', '250051'
    UNION ALL SELECT 'SOA', '250052'
    UNION ALL SELECT 'CHI', '250001'
    UNION ALL SELECT 'CHI', '250002'
    UNION ALL SELECT 'MED', '050001'
    UNION ALL SELECT 'MED', '050021'
    UNION ALL SELECT 'ENV', '055420'
    UNION ALL SELECT 'ENV', '055421'
    UNION ALL SELECT 'RIO', '054040'
    UNION ALL SELECT 'RIO', '054041'
    UNION ALL SELECT 'BAR', '080001'
    UNION ALL SELECT 'BAR', '080020'
    UNION ALL SELECT 'SOL', '083001'
    UNION ALL SELECT 'SOL', '083002'
    UNION ALL SELECT 'PUR', '081001'
    UNION ALL SELECT 'PUR', '081002'
) p
JOIN geo_cities c ON c.city_code = p.city_code;

INSERT INTO postal_code_zone_map (postal_code, zone_code, zone_name, active)
SELECT x.postal_code, x.zone_code, x.zone_name, b'1'
FROM (
    SELECT '250051' AS postal_code, 'LOW' AS zone_code, 'Zona baja' AS zone_name
    UNION ALL SELECT '250052', 'LOW', 'Zona baja'
    UNION ALL SELECT '250001', 'LOW', 'Zona baja'
    UNION ALL SELECT '250002', 'LOW', 'Zona baja'
    UNION ALL SELECT '055420', 'MEDIUM', 'Zona media'
    UNION ALL SELECT '055421', 'MEDIUM', 'Zona media'
    UNION ALL SELECT '054040', 'MEDIUM', 'Zona media'
    UNION ALL SELECT '054041', 'MEDIUM', 'Zona media'
    UNION ALL SELECT '083001', 'HIGH', 'Zona alta'
    UNION ALL SELECT '083002', 'HIGH', 'Zona alta'
    UNION ALL SELECT '081001', 'HIGH', 'Zona alta'
    UNION ALL SELECT '081002', 'HIGH', 'Zona alta'
) x
WHERE NOT EXISTS (
    SELECT 1
    FROM postal_code_zone_map existing
    WHERE existing.postal_code = x.postal_code
);
