CREATE TABLE IF NOT EXISTS user_ai_session
(
    chat_id BIGINT PRIMARY KEY,
    alarm   TIMESTAMP,
    note    TEXT
);