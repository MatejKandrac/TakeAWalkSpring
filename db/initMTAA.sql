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
    id          INT  NOT NULL PRIMARY KEY,
    picture     TEXT NULL,
    email       TEXT NOT NULL,
    "password"  TEXT NOT NULL,
    username    TEXT NOT NULL,
    bio         TEXT NULL,
    deviceToken TEXT NULL
);

CREATE TABLE events
(
    id             INT              NOT NULL PRIMARY KEY,
    ownerId        INT              NOT NULL,
    "name"         TEXT             NOT NULL,
    cancelled      BOOL             NOT NULL DEFAULT false,
    "start"        TIMESTAMP        NOT NULL,
    "end"          TIMESTAMP        NOT NULL,
    description    TEXT             NULL,
    ownerLat       DOUBLE PRECISION NULL,
    ownerLon       DOUBLE PRECISION NULL,
    actualLocation INT              NOT NULL DEFAULT 0,
    CONSTRAINT event_user FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TYPE STATUS AS ENUM ('Accepted', 'Declined', 'Pending');

CREATE TABLE invites
(
    id      INT    NOT NULL PRIMARY KEY,
    eventId INT    NOT NULL,
    userId  INT    NOT NULL,
    status  STATUS NOT NULL DEFAULT 'Pending',
    CONSTRAINT invites_event FOREIGN KEY (id) REFERENCES events (id),
    CONSTRAINT invites_user FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TABLE locations
(
    id        INT              NOT NULL PRIMARY KEY,
    eventId   INT              NOT NULL,
    "name"    TEXT             NOT NULL,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    visited   BOOL             NOT NULL DEFAULT false,
    "order"   INT              NOT NULL,
    CONSTRAINT locations_event FOREIGN KEY (id) REFERENCES events (id)
);

CREATE TABLE pictures
(
    id      INT  NOT NULL PRIMARY KEY,
    eventId INT  NOT NULL,
    link    TEXT NOT NULL,
    CONSTRAINT pictures_event FOREIGN KEY (id) REFERENCES events (id)
);

CREATE TABLE messages
(
    id        INT       NOT NULL PRIMARY KEY,
    eventId   INT       NOT NULL,
    userId    INT       NOT NULL,
    "message" TEXT      NOT NULL,
    sent      TIMESTAMP NOT NULL,
    CONSTRAINT messages_chat FOREIGN KEY (eventId) REFERENCES events (id),
    CONSTRAINT messages_user FOREIGN KEY (userId) REFERENCES users (id)
);


