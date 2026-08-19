-- Índices de alta performance para Chaves Estrangeiras e Joins da View de Catálogo

-- 1. Índices para a tabela properties
CREATE INDEX IF NOT EXISTS ix_properties_owner_id ON properties.properties (owner_id);
CREATE INDEX IF NOT EXISTS ix_properties_location_id ON properties.properties (location_id);
CREATE INDEX IF NOT EXISTS ix_properties_information_id ON properties.properties (information_id);

-- 2. Índice composto para subqueries de agendamentos/ocupações no catálogo
CREATE INDEX IF NOT EXISTS ix_schedules_property_status_date 
    ON properties.schedules (property_id, status, end_date);

-- 3. Índices para buscas e filtros de localização
CREATE INDEX IF NOT EXISTS ix_location_city_state ON location.location (city, state);
