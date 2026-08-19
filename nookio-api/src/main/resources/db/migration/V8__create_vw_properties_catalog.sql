ALTER TABLE properties.properties
    ADD COLUMN IF NOT EXISTS title VARCHAR(255) DEFAULT '' NOT NULL;

CREATE INDEX IF NOT EXISTS ix_properties_title ON properties.properties (title);

CREATE OR REPLACE VIEW properties.vw_properties_catalog AS
    SELECT
        p.id AS property_id,
        p.title AS title,
        o.id AS owner_id,
        CONCAT_WS(' ', o.first_name, o.last_name) AS owner_name,
        p.information_id AS information_id,
        (
            SELECT ph.file_id
                FROM properties.photos_properties ph
                WHERE ph.property_id = p.id
                ORDER BY ph.photo_order ASC
                LIMIT 1
        ) AS principal_photo_id,
        COALESCE(avg_rev.rating, 0.0) AS avaliation,
        (
            SELECT COUNT(s.id)
            FROM properties.schedules s
            WHERE s.property_id = p.id
              AND s.status NOT IN ('CANCELLED', 'REJECTED', 'EXPIRED')
        ) AS total_schedules,
        COALESCE(
            (
                SELECT jsonb_agg(
                    jsonb_build_object(
                        'start', s.start_date,
                        'end', s.end_date
                    )
                )
                FROM properties.schedules s
                WHERE s.property_id = p.id
                  AND s.end_date >= CURRENT_DATE
                  AND s.status NOT IN ('CANCELLED', 'REJECTED', 'EXPIRED')
            ),
            '[]'::jsonb
        ) AS occupations,
        p.location_id AS location_id
    FROM properties.properties p
    INNER JOIN users.users o ON p.owner_id = o.id
    INNER JOIN properties.property_informations pi ON p.information_id = pi.id
    LEFT JOIN (
        SELECT
            property_id,
            ROUND(AVG(rating), 2) AS rating
        FROM properties.avaliations
        GROUP BY property_id
    ) avg_rev ON avg_rev.property_id = p.id
    WHERE p.active = TRUE AND o.active = TRUE;
