CREATE TABLE users.users (
                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             phone_number VARCHAR(30)
);
ALTER TABLE properties.properties
    ADD CONSTRAINT fk_property_owner
        FOREIGN KEY (owner_id)
            REFERENCES users.users(id)
            ON DELETE RESTRICT;