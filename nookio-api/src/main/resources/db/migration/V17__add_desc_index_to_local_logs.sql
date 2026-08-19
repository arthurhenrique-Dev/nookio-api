DROP INDEX IF EXISTS management.idx_local_logs_timestamp;
CREATE INDEX idx_local_logs_timestamp_desc ON management.local_logs (timestamp DESC);
