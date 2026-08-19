CREATE TABLE properties.property_informations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    property_id BIGINT NOT NULL,
    property_type VARCHAR(50) NOT NULL,
    bedrooms INT,
    bathrooms INT,
    beds INT,
    max_guests INT,
    parking_spaces INT,
    area_sqm NUMERIC(8,2) NOT NULL,
    pools INT,
    next_to_beach BOOL,
    pet_friendly BOOL,
    has_wifi BOOL,
    has_air_conditioning BOOL,
    favorable_season VARCHAR(50),
    price_per_day NUMERIC(8,2) NOT NULL,
    cleaning_fee NUMERIC(8,2) NOT NULL
);

CREATE TABLE properties.properties (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    information_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    active BOOL NOT NULL,
    CONSTRAINT fk_properties_property_informations
        FOREIGN KEY (information_id)
            REFERENCES properties.property_informations(id)
            ON DELETE RESTRICT
);

ALTER TABLE properties.property_informations
    ADD CONSTRAINT fk_property_informations_properties
        FOREIGN KEY (property_id)
            REFERENCES properties.properties(id)
            ON DELETE RESTRICT;

CREATE TABLE properties.schedules (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    property_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    reservated_at TIMESTAMP,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    id_avaliation BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_PAYMENT',
    payment_id BIGINT,

    CONSTRAINT fk_schedules_property
        FOREIGN KEY (property_id)
            REFERENCES properties.properties(id)
            ON DELETE RESTRICT
);