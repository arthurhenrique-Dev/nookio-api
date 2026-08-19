CREATE TABLE files.files (

                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             type_file VARCHAR(100) NOT NULL,
                             url TEXT NOT NULL
);
CREATE TABLE properties.photos_properties (
                                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              property_id BIGINT NOT NULL,
                                              file_id BIGINT NOT NULL,
                                              photo_order INT NOT NULL DEFAULT 0,

                                              CONSTRAINT fk_photos_properties_property
                                                  FOREIGN KEY (property_id)
                                                      REFERENCES properties.properties(id)
                                                      ON DELETE CASCADE,

                                              CONSTRAINT fk_photos_properties_file
                                                  FOREIGN KEY (file_id)
                                                      REFERENCES files.files(id)
                                                      ON DELETE CASCADE,
                                              CONSTRAINT uq_photos_properties_property_file
                                                  UNIQUE (property_id, file_id),

                                              CONSTRAINT uq_photos_properties_property_order
                                                  UNIQUE (property_id, photo_order)
);
ALTER TABLE users.users
    ADD COLUMN first_name VARCHAR(255) NOT NULL,
    ADD COLUMN last_name VARCHAR(255) NOT NULL;