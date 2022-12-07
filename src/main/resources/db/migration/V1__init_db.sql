DROP TABLE IF EXISTS pack;
DROP TABLE IF EXISTS sack;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS delivery_point;
DROP TABLE IF EXISTS sack_packs;
DROP TABLE IF EXISTS delivery_point_packs;
DROP TABLE IF EXISTS delivery_point_sacks;
DROP TABLE IF EXISTS vehicle_packs;
DROP TABLE IF EXISTS vehicle_sacks;
DROP TABLE IF EXISTS vehicle_delivery_point;


CREATE TABLE pack
(
    id                  uuid UNIQUE,
    created_at          timestamp,
    updated_at          timestamp,
    barcode             varchar(11) NOT NULL,
    desi                int         NOT NULL,
    state               int         NOT NULL,
    delivery_point_code varchar(8),
    PRIMARY KEY (id)
);

CREATE TABLE sack
(
    id                  uuid UNIQUE,
    created_at          timestamp,
    updated_at          timestamp,
    sack_barcode        varchar(7) NOT NULL,
    desi                int        NOT NULL,
    free_desi           int        NOT NULL,
    size                int        NOT NULL,
    state               int        NOT NULL,
    delivery_point_code varchar(8),
    PRIMARY KEY (id)
);

CREATE TABLE vehicle
(
    id         uuid UNIQUE,
    created_at timestamp,
    updated_at timestamp,
    plate      varchar(8) NOT NULL,
    state      int        NOT NULL,
    PRIMARY KEY (id, plate)
);

CREATE TABLE delivery_point
(
    id                uuid UNIQUE,
    created_at        timestamp,
    updated_at        timestamp,
    code              varchar(8)  NOT NULL,
    place_of_delivery int         NOT NULL,
    name              varchar(20) NOT NULL,
    PRIMARY KEY (id, code)
);

CREATE TABLE sack_packs
(
    sack_id uuid NOT NULL,
    pack_id uuid NOT NULL,
    PRIMARY KEY (sack_id, pack_id)
);

ALTER TABLE sack_packs
    ADD CONSTRAINT fk_g66fgg4x8ccveg3ptutwmzccj FOREIGN KEY (sack_id) REFERENCES sack (id);
ALTER TABLE sack_packs
    ADD CONSTRAINT fk_j75d9s9xb26uy77uepp6bjfmt FOREIGN KEY (pack_id) REFERENCES pack (id);

CREATE TABLE delivery_point_packs
(
    delivery_point_id uuid NOT NULL,
    pack_id           uuid NOT NULL,
    PRIMARY KEY (delivery_point_id, pack_id)
);

ALTER TABLE delivery_point_packs
    ADD CONSTRAINT fk_fpzrrztw728zdjzj322mfwd6p FOREIGN KEY (delivery_point_id) REFERENCES delivery_point (id);
ALTER TABLE delivery_point_packs
    ADD CONSTRAINT fk_j75d9s9xb26uy77uepp6bjfmt FOREIGN KEY (pack_id) REFERENCES pack (id);

CREATE TABLE delivery_point_sacks
(
    delivery_point_id uuid NOT NULL,
    sack_id           uuid NOT NULL,
    PRIMARY KEY (delivery_point_id, sack_id)
);

ALTER TABLE delivery_point_sacks
    ADD CONSTRAINT fk_fpzrrztw728zdjzj322mfwd6p FOREIGN KEY (delivery_point_id) REFERENCES delivery_point (id);
ALTER TABLE delivery_point_sacks
    ADD CONSTRAINT fk_g66fgg4x8ccveg3ptutwmzccj FOREIGN KEY (sack_id) REFERENCES sack (id);

CREATE TABLE vehicle_packs
(
    vehicle_id uuid NOT NULL,
    pack_id    uuid NOT NULL,
    PRIMARY KEY (vehicle_id, pack_id)
);

ALTER TABLE vehicle_packs
    ADD CONSTRAINT fk_xbuyfc9qyk5jqa39976z2ccan FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);
ALTER TABLE vehicle_packs
    ADD CONSTRAINT fk_j75d9s9xb26uy77uepp6bjfmt FOREIGN KEY (pack_id) REFERENCES pack (id);

CREATE TABLE vehicle_sacks
(
    vehicle_id uuid NOT NULL,
    sack_id    uuid NOT NULL,
    PRIMARY KEY (vehicle_id, sack_id)
);

ALTER TABLE vehicle_sacks
    ADD CONSTRAINT fk_xbuyfc9qyk5jqa39976z2ccan FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);
ALTER TABLE vehicle_sacks
    ADD CONSTRAINT fk_g66fgg4x8ccveg3ptutwmzccj FOREIGN KEY (sack_id) REFERENCES sack (id);

CREATE TABLE vehicle_delivery_point
(
    vehicle_id        uuid NOT NULL,
    delivery_point_id uuid NOT NULL,
    PRIMARY KEY (vehicle_id, delivery_point_id)
);

ALTER TABLE vehicle_delivery_point
    ADD CONSTRAINT fk_xbuyfc9qyk5jqa39976z2ccan FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);
ALTER TABLE vehicle_delivery_point
    ADD CONSTRAINT fk_fpzrrztw728zdjzj322mfwd6p FOREIGN KEY (delivery_point_id) REFERENCES delivery_point (id);

