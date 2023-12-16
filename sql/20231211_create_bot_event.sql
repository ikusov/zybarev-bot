create table if not exists bot_event (
    id serial primary key,
    event_date timestamptz,
    command varchar(64),
    extended_chat_id varchar(64),
    user_id bigint
);