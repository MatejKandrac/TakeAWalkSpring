DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS chats;
DROP TABLE IF EXISTS pictures;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS invites;
DROP TYPE IF EXISTS STATUS;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id           SERIAL NOT NULL PRIMARY KEY,
    picture      TEXT   NULL,
    email        TEXT   NOT NULL,
    "password"   TEXT   NOT NULL,
    username     TEXT   NOT NULL UNIQUE ,
    bio          TEXT   NULL,
    device_token TEXT   NULL
);

CREATE TABLE events
(
    id              SERIAL           NOT NULL PRIMARY KEY,
    owner_id        INT              NOT NULL,
    "name"          TEXT             NOT NULL,
    cancelled       BOOL             NOT NULL DEFAULT false,
    "start"         TIMESTAMP        NOT NULL,
    end_date        TIMESTAMP        NOT NULL,
    description     TEXT             NULL,
    owner_lat       DOUBLE PRECISION NULL,
    owner_lon       DOUBLE PRECISION NULL,
    actual_location INT              NOT NULL DEFAULT 0,
    CONSTRAINT event_user FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TYPE STATUS AS ENUM ('ACCEPTED', 'DECLINED', 'PENDING');


CREATE TABLE invites
(
    id       SERIAL NOT NULL PRIMARY KEY,
    event_id INT    NOT NULL,
    user_id  INT    NOT NULL,
    status   STATUS NOT NULL DEFAULT 'PENDING',
    CONSTRAINT invites_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT invites_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE locations
(
    id               SERIAL           NOT NULL PRIMARY KEY,
    event_id         INT              NOT NULL,
    "name"           TEXT             NOT NULL,
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    "location_order" INT              NOT NULL,
    CONSTRAINT locations_event FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE pictures
(
    id       SERIAL  NOT NULL PRIMARY KEY,
    event_id INT     NOT NULL,
    link     TEXT    NOT NULL,
    deleted  BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT pictures_event FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE messages
(
    id        SERIAL    NOT NULL PRIMARY KEY,
    event_id  INT       NOT NULL,
    user_id   INT       NOT NULL,
    "message" TEXT      NOT NULL,
    sent      TIMESTAMP NOT NULL,
    CONSTRAINT messages_chat FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT messages_user FOREIGN KEY (user_id) REFERENCES users (id)
);

