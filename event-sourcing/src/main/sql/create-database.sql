CREATE TYPE EntityType AS ENUM ('USER');

CREATE TYPE GateEventType AS ENUM ('ENTER', 'EXIT');

CREATE TABLE ids_pool
(
    entity EntityType NOT NULL PRIMARY KEY,
    max_id INT        NOT NULL
);

INSERT INTO ids_pool
VALUES ('USER', 0);

CREATE TABLE users
(
    user_id INT          NOT NULL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL
);

CREATE TABLE gate_events
(
    user_id    INT           NOT NULL PRIMARY KEY REFERENCES users (user_id),
    event_id   INT           NOT NULL,
    event_type GateEventType NOT NULL,
    event_time TIMESTAMP     NOT NULL
);

CREATE TABLE subscription_events
(
    user_id  INT       NOT NULL PRIMARY KEY REFERENCES users (user_id),
    event_id INT       NOT NULL,
    end_time TIMESTAMP NOT NULL
);
