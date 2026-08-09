-- 1. Adicionar coluna de foto de perfil na tabela de usuários
ALTER TABLE users.users ADD COLUMN IF NOT EXISTS profile_file_id INT;

DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_users_profile_file'
    ) THEN
        ALTER TABLE users.users 
        ADD CONSTRAINT fk_users_profile_file 
        FOREIGN KEY (profile_file_id) REFERENCES files.files (id) ON DELETE SET NULL;
    END IF;
END $$;

CREATE OR REPLACE VIEW messages.vw_user_conversations AS
WITH ranked_messages AS (
    SELECT 
        m.id AS last_message_id,
        m.sender_id,
        m.receiver_id,
        m.file_id,
        m.content AS last_message_content,
        m.sended_at AS last_message_sended_at,
        m.visualized AS last_message_visualized,
        CASE 
            WHEN m.sender_id < m.receiver_id THEN m.sender_id || '_' || m.receiver_id
            ELSE m.receiver_id || '_' || m.sender_id
        END AS pair_key,
        ROW_NUMBER() OVER (
            PARTITION BY CASE 
                WHEN m.sender_id < m.receiver_id THEN m.sender_id || '_' || m.receiver_id
                ELSE m.receiver_id || '_' || m.sender_id
            END 
            ORDER BY m.sended_at DESC
        ) AS rn
    FROM messages.messages m
)
SELECT 
    rm.last_message_id,
    rm.pair_key,
    rm.sender_id,
    CONCAT_WS(' ', su.first_name, su.last_name) AS sender_fullname,
    rm.receiver_id,
    CONCAT_WS(' ', ru.first_name, ru.last_name) AS receiver_fullname,
    rm.last_message_content,
    rm.file_id AS last_message_file_id,
    f.url AS last_message_file_url,
    f.type_file AS last_message_file_type,
    rm.last_message_sended_at,
    rm.last_message_visualized,
    spf.url AS sender_profile_photo_url,
    rpf.url AS receiver_profile_photo_url
FROM ranked_messages rm
JOIN users.users su ON su.id = rm.sender_id
LEFT JOIN files.files spf ON spf.id = su.profile_file_id
JOIN users.users ru ON ru.id = rm.receiver_id
LEFT JOIN files.files rpf ON rpf.id = ru.profile_file_id
LEFT JOIN files.files f ON f.id = rm.file_id
WHERE rm.rn = 1;

CREATE OR REPLACE VIEW messages.vw_conversation_messages AS
SELECT 
    m.id AS message_id,
    m.sender_id,
    CONCAT_WS(' ', su.first_name, su.last_name) AS sender_fullname,
    m.receiver_id,
    CONCAT_WS(' ', ru.first_name, ru.last_name) AS receiver_fullname,
    m.content,
    m.file_id,
    f.url AS file_url,
    f.type_file AS file_type,
    m.sended_at,
    m.visualized,
    spf.url AS sender_profile_photo_url,
    rpf.url AS receiver_profile_photo_url
FROM messages.messages m
JOIN users.users su ON su.id = m.sender_id
LEFT JOIN files.files spf ON spf.id = su.profile_file_id
JOIN users.users ru ON ru.id = m.receiver_id
LEFT JOIN files.files rpf ON rpf.id = ru.profile_file_id
LEFT JOIN files.files f ON f.id = m.file_id;

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
    p.location_id AS location_id,
    (pi.price_per_day + COALESCE(pi.cleaning_fee, 0)) AS total_price,
    opf.url AS owner_profile_photo_url
FROM properties.properties p
INNER JOIN users.users o ON p.owner_id = o.id
LEFT JOIN files.files opf ON opf.id = o.profile_file_id
INNER JOIN properties.property_informations pi ON p.information_id = pi.id
LEFT JOIN (
    SELECT
        property_id,
        ROUND(AVG(rating), 2) AS rating
    FROM properties.avaliations
    GROUP BY property_id
) avg_rev ON avg_rev.property_id = p.id
WHERE p.active = TRUE AND o.active = TRUE;
