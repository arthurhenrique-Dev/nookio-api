CREATE TABLE IF NOT EXISTS properties.priority (
    params TEXT PRIMARY KEY,
    searchs INT NOT NULL DEFAULT 1,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_priority_searchs_timestamp
ON properties.priority (searchs DESC, timestamp DESC);

CREATE TABLE IF NOT EXISTS properties.search_logs (
    id BIGSERIAL PRIMARY KEY,
    canonical_params TEXT NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
