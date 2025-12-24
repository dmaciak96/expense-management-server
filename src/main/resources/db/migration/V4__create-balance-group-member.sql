CREATE TABLE balance_group_member
(
    balance_group_id UUID NOT NULL,
    user_id          UUID NOT NULL,

    CONSTRAINT pk_balance_group_member
        PRIMARY KEY (balance_group_id, user_id),

    CONSTRAINT fk_balance_group_member_balance_group
        FOREIGN KEY (balance_group_id)
            REFERENCES balance_group_entity (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_balance_group_member_user
        FOREIGN KEY (user_id)
            REFERENCES user_entity (id)
            ON DELETE CASCADE
);