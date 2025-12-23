CREATE TABLE IF NOT EXISTS balance_group_entity (
    id              UUID            NOT NULL PRIMARY KEY,
    version         INT             NOT NULL DEFAULT 0,
    created_by_id   UUID            NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NULL,
    group_name      TEXT            NOT NULL,

    CONSTRAINT fk_balance_group_created_by
        FOREIGN KEY (created_by_id)
        REFERENCES user_entity(id)
);
