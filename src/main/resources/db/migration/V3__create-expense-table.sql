CREATE TABLE IF NOT EXISTS expense_entity (
    id                  UUID                NOT NULL PRIMARY KEY,
    version             INT                 NOT NULL DEFAULT 0,
    created_by_id       UUID                NOT NULL,
    created_at          TIMESTAMP           NOT NULL,
    updated_at          TIMESTAMP           NULL,
    name                TEXT                NOT NULL,
    amount              DOUBLE PRECISION    NOT NULL,
    balance_group_id    UUID                NOT NULL,
    split_type          TEXT                NOT NULL,

    CONSTRAINT fk_expense_balance_group
        FOREIGN KEY (balance_group_id)
        REFERENCES balance_group_entity (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_expense_created_by
        FOREIGN KEY (created_by_id)
        REFERENCES user_entity(id)
)