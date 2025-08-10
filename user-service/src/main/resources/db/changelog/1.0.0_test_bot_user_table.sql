CREATE TABLE IF NOT EXISTS bot_users
(
    chat_id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    tel_num VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK ( role IN ('ADMIN', 'USER')),
    is_registered BOOLEAN NOT NULL DEFAULT false
);