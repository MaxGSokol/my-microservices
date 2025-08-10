CREATE TABLE IF NOT EXISTS session_history_context
(
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL CHECK ( role IN ('USER', 'ASSISTANT', 'SYSTEM')),
    content TEXT NOT NULL,
    time_stamp TIMESTAMP NOT NULL
);