CREATE SCHEMA IF NOT EXISTS messages;

CREATE TABLE IF NOT EXISTS messages.messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    file_id INT,
    content TEXT,
    sended_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    visualized BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users.users (id),
    CONSTRAINT fk_messages_receiver FOREIGN KEY (receiver_id) REFERENCES users.users (id),
    CONSTRAINT fk_messages_file FOREIGN KEY (file_id) REFERENCES files.files (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_sender_receiver_sended ON messages.messages (sender_id, receiver_id, sended_at DESC);
CREATE INDEX IF NOT EXISTS idx_messages_receiver_sender_sended ON messages.messages (receiver_id, sender_id, sended_at DESC);

CREATE INDEX IF NOT EXISTS idx_messages_unread ON messages.messages (receiver_id, sender_id) WHERE visualized = FALSE;

CREATE INDEX IF NOT EXISTS idx_messages_file ON messages.messages (file_id) WHERE file_id IS NOT NULL;

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
    rm.last_message_visualized
FROM ranked_messages rm
JOIN users.users su ON su.id = rm.sender_id
JOIN users.users ru ON ru.id = rm.receiver_id
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
    m.visualized
FROM messages.messages m
JOIN users.users su ON su.id = m.sender_id
JOIN users.users ru ON ru.id = m.receiver_id
LEFT JOIN files.files f ON f.id = m.file_id;
