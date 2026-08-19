CREATE SCHEMA IF NOT EXISTS management;

CREATE TABLE IF NOT EXISTS emails (
    id UUID PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS management.local_logs (
    id SERIAL PRIMARY KEY,
    ip VARCHAR(45),
    resource VARCHAR(255),
    operation VARCHAR(255),
    result INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_local_logs_timestamp_desc ON management.local_logs (timestamp DESC);
