CREATE SCHEMA IF NOT EXISTS management;

CREATE TABLE management.local_logs (
    id SERIAL PRIMARY KEY,
    ip VARCHAR(45),
    resource VARCHAR(255),
    operation VARCHAR(255),
    result INT,
    timestamp TIMESTAMP
);

CREATE INDEX idx_local_logs_timestamp ON management.local_logs (timestamp);
