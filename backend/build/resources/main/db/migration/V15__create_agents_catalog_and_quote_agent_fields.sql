CREATE TABLE agents_catalog (
    agent_code VARCHAR(32) NOT NULL PRIMARY KEY,
    agent_name VARCHAR(255) NOT NULL,
    channel VARCHAR(64) NOT NULL,
    branch VARCHAR(120) NOT NULL,
    active BIT NOT NULL DEFAULT 1
);

ALTER TABLE quotes
    ADD COLUMN agent_code VARCHAR(32) NULL,
    ADD COLUMN agent_name_snapshot VARCHAR(255) NULL;

ALTER TABLE quotes
    ADD CONSTRAINT fk_quotes_agent_code
        FOREIGN KEY (agent_code) REFERENCES agents_catalog (agent_code);

CREATE INDEX idx_quotes_agent_code ON quotes (agent_code);
CREATE INDEX idx_agents_active ON agents_catalog (active);

INSERT INTO agents_catalog (agent_code, agent_name, channel, branch, active) VALUES
    ('AGT-001', 'Juan Perez', 'BROKER', 'Bogota Centro', 1),
    ('AGT-002', 'Maria Gomez', 'DIRECT', 'Medellin Norte', 1);
