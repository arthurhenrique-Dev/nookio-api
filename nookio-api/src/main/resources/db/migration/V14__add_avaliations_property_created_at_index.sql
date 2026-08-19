CREATE INDEX IF NOT EXISTS ix_avaliations_property_created_at_desc
    ON properties.avaliations (property_id, created_at DESC);
