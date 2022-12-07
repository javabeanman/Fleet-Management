/*We expect the shipment with barcode P8988000120 to remain in "created" state.*/
INSERT INTO pack(id, created_at, updated_at, barcode, desi, state, delivery_point_code)
VALUES ('531e4cdd-bb78-4769-a0c7-cb948a9f1238', CURRENT_TIMESTAMP, NULL, 'P8988000120', 4, 1, NULL);

/*We are waiting for a log for barcodes P8988000121 and C725799 (due to attempted
delivery to the wrong location).*/
INSERT INTO pack(id, created_at, updated_at, barcode, desi, state, delivery_point_code)
VALUES ('d66bff71-6847-4c15-9c4a-02e64fc4e4c8', CURRENT_TIMESTAMP, NULL, 'P8988000121', 2, 2, NULL);

INSERT INTO sack(id, created_at, updated_at, sack_barcode, desi, free_desi, size, state, delivery_point_code)
VALUES ('68b396b3-f595-4398-a76a-a19d9547ee0e', CURRENT_TIMESTAMP, NULL, 'C725799', 50, 48, 1, 3, NULL);

INSERT INTO sack_packs(sack_id, pack_id)
VALUES ('68b396b3-f595-4398-a76a-a19d9547ee0e', 'd66bff71-6847-4c15-9c4a-02e64fc4e4c8');

INSERT INTO vehicle(id, created_at, updated_at, plate, state)
VALUES ('00bf0718-12f6-4c11-9230-0c0d2e82f8c6', CURRENT_TIMESTAMP, NULL, '34TL310', 4);

INSERT INTO vehicle_sacks(vehicle_id, sack_id)
VALUES ('00bf0718-12f6-4c11-9230-0c0d2e82f8c6', '68b396b3-f595-4398-a76a-a19d9547ee0e');

INSERT INTO delivery_point(id, created_at, updated_at, code, place_of_delivery, name)
VALUES ('9ae3b6be-7457-44f2-b930-ba85aba3cd63', CURRENT_TIMESTAMP, NULL, 'DP230001', 1, 'Example DP230001');

INSERT INTO vehicle_delivery_point(vehicle_id, delivery_point_id)
VALUES ('00bf0718-12f6-4c11-9230-0c0d2e82f8c6', '9ae3b6be-7457-44f2-b930-ba85aba3cd63');

/*We expect barcode P8988000121 to remain in the "loaded" state.*/
INSERT INTO vehicle(id, created_at, updated_at, plate, state)
VALUES ('7d9a32b6-632f-4dd5-a03a-602efc9f972c', CURRENT_TIMESTAMP, NULL, '28BA242', 3);

INSERT INTO delivery_point(id, created_at, updated_at, code, place_of_delivery, name)
VALUES ('71f11843-1ac6-4def-9891-c1495055fa13', CURRENT_TIMESTAMP, NULL, 'DP230002', 1, 'Example DP230002');

INSERT INTO vehicle_delivery_point(vehicle_id, delivery_point_id)
VALUES ('7d9a32b6-632f-4dd5-a03a-602efc9f972c', '71f11843-1ac6-4def-9891-c1495055fa13');

INSERT INTO pack(id, created_at, updated_at, barcode, desi, state, delivery_point_code)
VALUES ('a096b332-65a3-440b-bd81-e12754f0c675', CURRENT_TIMESTAMP, NULL, 'P8988000121', 1, 3, NULL);

INSERT INTO vehicle_packs(vehicle_id, pack_id)
VALUES ('7d9a32b6-632f-4dd5-a03a-602efc9f972c', 'a096b332-65a3-440b-bd81-e12754f0c675');

/*We expect sack C725800 to be in the "unloaded" state.*/
INSERT INTO pack(id, created_at, updated_at, barcode, desi, state, delivery_point_code)
VALUES ('6a1e5fe7-4030-4171-b0e0-5c4ca7524279', CURRENT_TIMESTAMP, NULL, 'P8988000123', 6, 2, NULL);

INSERT INTO sack(id, created_at, updated_at, sack_barcode, desi, free_desi, size, state, delivery_point_code)
VALUES ('23ec8127-e9c3-48b2-9fbe-8f172e6572be', CURRENT_TIMESTAMP, NULL, 'C725800', 50, 46, 1, 3, NULL);

INSERT INTO sack_packs(sack_id, pack_id)
VALUES ('23ec8127-e9c3-48b2-9fbe-8f172e6572be', '6a1e5fe7-4030-4171-b0e0-5c4ca7524279');

INSERT INTO delivery_point(id, created_at, updated_at, code, place_of_delivery, name)
VALUES ('65a26e4a-e450-4bb5-860b-f1b24ece7399', CURRENT_TIMESTAMP, NULL, 'DP230003', 3, 'Example DP230003');

INSERT INTO delivery_point_sacks(delivery_point_id, sack_id)
VALUES ('65a26e4a-e450-4bb5-860b-f1b24ece7399', '23ec8127-e9c3-48b2-9fbe-8f172e6572be');

/*We expect barcode P8988000122 to be in the "unloaded" state.*/
INSERT INTO pack(id, created_at, updated_at, barcode, desi, state, delivery_point_code)
VALUES ('c37fc6df-9783-4fc5-9c5b-32a6c113d815', CURRENT_TIMESTAMP, NULL, 'P8988000124', 6, 4, NULL);

INSERT INTO delivery_point(id, created_at, updated_at, code, place_of_delivery, name)
VALUES ('2935aebb-d774-432c-9924-11f71096a6fe', CURRENT_TIMESTAMP, NULL, 'DP230004', 2, 'Example DP230004');

INSERT INTO delivery_point_packs(delivery_point_id, pack_id)
VALUES ('2935aebb-d774-432c-9924-11f71096a6fe', 'c37fc6df-9783-4fc5-9c5b-32a6c113d815');

/*We expect barcode P8988000126 to be in the "unloaded" state.*/
INSERT INTO pack(id, created_at, updated_at, barcode, desi, state, delivery_point_code)
VALUES ('f2578e2d-0265-4995-9bc4-dd1269580c66', CURRENT_TIMESTAMP, NULL, 'P8988000125', 2, 4, NULL);

INSERT INTO delivery_point(id, created_at, updated_at, code, place_of_delivery, name)
VALUES ('3e29bcb6-b72d-478b-ae0d-4bba7a095e19', CURRENT_TIMESTAMP, NULL, 'DP230005', 2, 'Example DP230005');

INSERT INTO delivery_point_packs(delivery_point_id, pack_id)
VALUES ('3e29bcb6-b72d-478b-ae0d-4bba7a095e19', 'f2578e2d-0265-4995-9bc4-dd1269580c66');