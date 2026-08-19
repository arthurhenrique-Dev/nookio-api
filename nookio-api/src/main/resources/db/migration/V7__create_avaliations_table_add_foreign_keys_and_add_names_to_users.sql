CREATE TABLE properties.avaliations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    avaliator_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    rating NUMERIC(3,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500),

    CONSTRAINT fk_avaliations_avaliator
        FOREIGN KEY (avaliator_id)
        REFERENCES users.users(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_avaliations_property
        FOREIGN KEY (property_id)
        REFERENCES properties.properties(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_avaliations_owner
        FOREIGN KEY (owner_id)
        REFERENCES users.users(id)
        ON DELETE RESTRICT
);

ALTER TABLE properties.schedules
    ADD CONSTRAINT fk_schedules_avaliation
        FOREIGN KEY (id_avaliation)
        REFERENCES properties.avaliations(id)
        ON DELETE SET NULL;

ALTER TABLE users.users
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS last_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS active BOOL DEFAULT TRUE NOT NULL;
