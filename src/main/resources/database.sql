create table
    user(
            uuid varchar(36) not null primary key,
            username varchar(50) not null,
            password varchar(60) not null,
            admin boolean not null default false
);