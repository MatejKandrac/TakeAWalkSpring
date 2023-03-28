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
    username     TEXT   NOT NULL,
    bio          TEXT   NULL,
    device_token TEXT   NULL
);

insert into users
values ('1', 'some picture', 'some email', 'some password', 'some username', 'some description', 'some device token');
insert into users
values ('2', 'next picture', 'next email', 'next password', 'next username', 'next description', 'next device token');
insert into users
values ('3', 'last picture', 'last email', 'last password', 'last username', 'last description', 'last device token');
insert into users
values ('4', 'last picture', 'last email', 'last password', 'Patreax', 'last description', 'last device token');

insert into users
values ('5', 'picture5', 'email5', 'password5', 'Patreax', 'description5', 'devicetoken5');
insert into users
values ('6', 'picture6', 'email6', 'password6', 'Patreax', 'description6', 'devicetoken6');

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

-- insert into events
-- values ('100', '5', 'firstevent', 'false', '2020-06-22 19:10:25-07', '2020-06-22 19:10:25-07', 'firstdescription',
--         '100', '100', '0');
-- insert into events
-- values ('101', '5', 'firstevent', 'false', '2020-06-22 19:10:25-07', '2020-06-22 19:10:25-07', 'firstdescription',
--         '100', '100', '0');
-- insert into events
-- values ('102', '5', 'firstevent', 'false', '2020-06-22 19:10:25-07', '2020-06-22 19:10:25-07', 'firstdescription',
--         '100', '100', '0');
-- insert into events
-- values ('103', '5', 'firstevent', 'false', '2020-06-22 19:10:25-07', '2020-06-22 19:10:25-07', 'firstdescription',
--         '100', '100', '0');

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

-- insert into invites
-- values ('1', '100', '5', 'Pending');
-- insert into invites
-- values ('2', '101', '5', 'Pending');
-- insert into invites
-- values ('3', '102', '5', 'Pending');
-- insert into invites
-- values ('4', '103', '6', 'Pending');

CREATE TABLE locations
(
    id               SERIAL           NOT NULL PRIMARY KEY,
    event_id         INT              NOT NULL,
    "name"           TEXT             NOT NULL,
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    visited          BOOL             NOT NULL DEFAULT false,
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

-- insert into messages
-- values ('1', 100, '5', 'first ever test message', '2020-06-22 19:10:25-07');
-- insert into messages
-- values ('2', '100', '5', 'second ever test message', '2020-06-22 19:10:25-07');
-- insert into messages
-- values ('3', '100', '5', 'third ever test message', '2020-06-22 19:10:25-07');
-- insert into messages
-- values ('4', '100', '5', 'fourth ever test message', '2020-06-22 19:10:25-07');
-- insert into messages
-- values ('5', '100', '5', 'fifth ever test message', '2020-06-22 19:10:25-07');
