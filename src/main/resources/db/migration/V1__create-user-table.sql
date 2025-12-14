CREATE TABLE IF NOT EXISTS user_entity
(
    id                  UUID        NOT NULL PRIMARY KEY,
    email               TEXT        NOT NULL UNIQUE,
    nickname            TEXT,
    password_hash       TEXT        NOT NULL,
    role                TEXT        NOT NULL,
    is_email_verified   BOOLEAN     NOT NULL,
    created_at          TIMESTAMP   NOT NULL,
    updated_at          TIMESTAMP,
    last_login_at       TIMESTAMP,
    account_status      TEXT        NOT NULL,
    version             INT         DEFAULT 0
);