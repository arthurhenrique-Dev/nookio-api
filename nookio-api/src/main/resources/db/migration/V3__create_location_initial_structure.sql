CREATE TABLE location.location (
                                   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   street VARCHAR(255),
                                   number VARCHAR(30),
                                   complement VARCHAR(255),
                                   neighborhood VARCHAR(100),
                                   city VARCHAR(100),
                                   state VARCHAR(50),
                                   zip_code VARCHAR(20),
                                   country VARCHAR(100),
                                   latitude NUMERIC(10, 8),
                                   longitude NUMERIC(11, 8)
);

ALTER TABLE properties.properties
    ADD CONSTRAINT fk_property_location
        FOREIGN KEY (location_id)
            REFERENCES location.location(id)
            ON DELETE RESTRICT;