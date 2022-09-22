create table invite (
    user_id bigserial not null,
    expiration_date timestamp,
    token varchar(255) UNIQUE NOT NULL,
    primary key (user_id)
)
