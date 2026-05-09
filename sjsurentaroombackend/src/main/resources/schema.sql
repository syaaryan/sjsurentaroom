CREATE TABLE IF NOT EXISTS users (
    user_id  BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255),
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    role     VARCHAR(32),
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id               BIGINT       NOT NULL AUTO_INCREMENT,
    name                  VARCHAR(255),
    building              VARCHAR(255),
    capacity              INT,
    amenities             TEXT,
    description           TEXT,
    available_for_booking BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (room_id)
);

CREATE TABLE IF NOT EXISTS availability (
    slot_id    BIGINT      NOT NULL AUTO_INCREMENT,
    room_id    BIGINT      NOT NULL,
    start_time DATETIME(6),
    end_time   DATETIME(6),
    status     VARCHAR(32) NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (slot_id),
    CONSTRAINT fk_avail_room FOREIGN KEY (room_id) REFERENCES rooms (room_id)
);

CREATE TABLE IF NOT EXISTS reservation (
    reservation_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL,
    room_id        BIGINT       NOT NULL,
    slot_id        BIGINT       NOT NULL UNIQUE,
    status         VARCHAR(32),
    notes          VARCHAR(255),
    PRIMARY KEY (reservation_id),
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_res_room FOREIGN KEY (room_id) REFERENCES rooms (room_id),
    CONSTRAINT fk_res_slot FOREIGN KEY (slot_id) REFERENCES availability (slot_id)
);
