CREATE TABLE IF NOT EXISTS user_tel_book
(
    chat_id  BIGINT PRIMARY KEY,
    tel_book JSONB
);