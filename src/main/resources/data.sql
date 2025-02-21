INSERT INTO users (USER_ID, USER_EMAIL, USER_NICK, USER_PHONE, USER_DEVICE_TOKEN) VALUES
                                                                                      ('user1', 'user1@example.com', 'User One', '010-1234-5678', 'deviceToken1'),
                                                                                      ('user2', 'user2@example.com', 'User Two', NULL, 'deviceToken2'),
                                                                                      ('user3', 'user3@example.com', 'User Three', '010-8765-4321', NULL),
                                                                                      ('user4', 'user4@example.com', 'User Four', NULL, NULL),
                                                                                      ('user5', 'user5@example.com', 'User Five', '010-1122-3344', 'deviceToken5');

INSERT INTO ticket (ticket_id, arrive_time, depart_date, depart_time, price, ticket_type, arrive_station, depart_station)
VALUES
    (1, '0810', '20241009', '0508', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (2, '1105', '20241009', '0544', '27600원', '무궁화호', '여수EXPO', '용산'),
    (3, '1019', '20241009', '0710', '47200원', 'KTX', '여수EXPO', '용산'),
    (4, '1156', '20241009', '0714', '41100원', 'ITX-새마을', '여수EXPO', '용산'),
    (5, '1123', '20241009', '0743', '46000원', 'KTX', '여수EXPO', '용산'),
    (6, '1146', '20241009', '0840', '47200원', 'KTX', '여수EXPO', '용산'),
    (7, '1513', '20241009', '0938', '27600원', '무궁화호', '여수EXPO', '용산'),
    (8, '1426', '20241009', '0947', '0원', 'ITX-마음', '여수EXPO', '용산'),
    (9, '1303', '20241009', '0953', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (10, '1401', '20241009', '1053', '47200원', 'KTX', '여수EXPO', '용산'),
    (11, '1521', '20241009', '1218', '47200원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (12, '1543', '20241009', '1245', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (13, '1710', '20241009', '1410', '47200원', 'KTX', '여수EXPO', '용산'),
    (14, '1828', '20241009', '1438', '46000원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (15, '2020', '20241009', '1504', '27600원', '무궁화호', '여수EXPO', '용산'),
    (16, '2122', '20241009', '1638', '41100원', 'ITX-새마을', '여수EXPO', '용산'),
    (17, '1954', '20241009', '1643', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (18, '2143', '20241009', '1718', '0원', 'ITX-마음', '여수EXPO', '용산'),
    (19, '2042', '20241009', '1744', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (20, '2151', '20241009', '1848', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (21, '0026', '20241010', '1915', '27600원', '무궁화호', '여수EXPO', '용산'),
    (22, '2309', '20241009', '2008', '47200원', 'KTX', '여수EXPO', '용산'),
    (23, '2335', '20241009', '2033', '47200원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (24, '0035', '20241010', '2148', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (25, '0810', '20241010', '0508', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    -- 26번 티켓
    (26, '1105', '20241010', '0544', '27600원', '무궁화호', '여수EXPO', '용산'),

    -- 27번 티켓
    (27, '1019', '20241010', '0710', '47200원', 'KTX', '여수EXPO', '용산'),

    -- 28번 티켓
    (28, '1156', '20241010', '0714', '41100원', 'ITX-새마을', '여수EXPO', '용산'),

    -- 29번 티켓
    (29, '1123', '20241010', '0743', '46000원', 'KTX', '여수EXPO', '용산'),

    -- 30번 티켓
    (30, '1146', '20241010', '0840', '47200원', 'KTX', '여수EXPO', '용산'),

    -- 31번 티켓
    (31, '1513', '20241010', '0938', '27600원', '무궁화호', '여수EXPO', '용산'),

    -- 32번 티켓
    (32, '1426', '20241010', '0947', '0원', 'ITX-마음', '여수EXPO', '용산'),

    -- 33번 티켓
    (33, '1303', '20241010', '0953', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),

    -- 34번 티켓
    (34, '1401', '20241010', '1053', '47200원', 'KTX', '여수EXPO', '용산'),

    -- 35번 티켓
    (35, '1521', '20241010', '1218', '47200원', 'KTX-산천(A-type)', '여수EXPO', '용산'),

    -- 36번 티켓
    (36, '1543', '20241010', '1245', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),

    -- 37번 티켓
    (37, '1710', '20241010', '1410', '47200원', 'KTX', '여수EXPO', '용산'),

    -- 38번 티켓
    (38, '1828', '20241010', '1438', '46000원', 'KTX-산천(A-type)', '여수EXPO', '용산'),

    -- 39번 티켓
    (39, '2020', '20241010', '1504', '27600원', '무궁화호', '여수EXPO', '용산'),

    -- 40번 티켓
    (40, '2122', '20241010', '1638', '41100원', 'ITX-새마을', '여수EXPO', '용산'),

    -- 41번 티켓
    (41, '1954', '20241010', '1643', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),

    -- 42번 티켓
    (42, '2143', '20241010', '1718', '0원', 'ITX-마음', '여수EXPO', '용산'),

    -- 43번 티켓
    (43, '2042', '20241010', '1744', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),

    -- 44번 티켓
    (44, '2151', '20241010', '1848', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),

    -- 45번 티켓
    (45, '0026', '20241011', '1915', '27600원', '무궁화호', '여수EXPO', '용산'),

    -- 46번 티켓
    (46, '2309', '20241010', '2008', '47200원', 'KTX', '여수EXPO', '용산'),

    -- 47번 티켓
    (47, '2335', '20241010', '2033', '47200원', 'KTX-산천(A-type)', '여수EXPO', '용산'),

    -- 48번 티켓
    (48, '0035', '20241011', '2148', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (49, '0810', '20241011', '0508', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (50, '1105', '20241011', '0544', '0원', '무궁화호', '여수EXPO', '용산'),
    (51, '1019', '20241011', '0710', '0원', 'KTX', '여수EXPO', '용산'),
    (52, '1156', '20241011', '0714', '0원', 'ITX-새마을', '여수EXPO', '용산'),
    (53, '1123', '20241011', '0743', '0원', 'KTX', '여수EXPO', '용산'),
    (54, '1146', '20241011', '0840', '0원', 'KTX', '여수EXPO', '용산'),
    (55, '1513', '20241011', '0938', '0원', '무궁화호', '여수EXPO', '용산'),
    (56, '1426', '20241011', '0947', '0원', 'ITX-마음', '여수EXPO', '용산'),
    (57, '1303', '20241011', '0953', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (58, '1401', '20241011', '1053', '0원', 'KTX', '여수EXPO', '용산'),
    (59, '1521', '20241011', '1218', '0원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (60, '1710', '20241011', '1410', '0원', 'KTX', '여수EXPO', '용산'),
    (61, '1830', '20241011', '1438', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (62, '2020', '20241011', '1504', '0원', '무궁화호', '여수EXPO', '용산'),
    (63, '2122', '20241011', '1638', '0원', 'ITX-새마을', '여수EXPO', '용산'),
    (64, '1954', '20241011', '1643', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (65, '2143', '20241011', '1718', '0원', 'ITX-마음', '여수EXPO', '용산'),
    (66, '2042', '20241011', '1744', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (67, '2151', '20241011', '1848', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (68, '0026', '20241011', '1915', '0원', '무궁화호', '여수EXPO', '용산'),
    (69, '2309', '20241011', '2008', '0원', 'KTX', '여수EXPO', '용산'),
    (70, '2335', '20241011', '2033', '0원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (71, '0035', '20241011', '2148', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (72, '0810', '20241012', '0508', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (73, '1105', '20241012', '0544', '0원', '무궁화호', '여수EXPO', '용산'),
    (74, '1019', '20241012', '0710', '47200원', 'KTX', '여수EXPO', '용산'),
    (75, '1156', '20241012', '0714', '0원', 'ITX-새마을', '여수EXPO', '용산'),
    (76, '1123', '20241012', '0743', '46000원', 'KTX', '여수EXPO', '용산'),
    (77, '1045', '20241012', '0747', '0원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (78, '1146', '20241012', '0840', '47200원', 'KTX', '여수EXPO', '용산'),
    (79, '1513', '20241012', '0938', '0원', '무궁화호', '여수EXPO', '용산'),
    (80, '1426', '20241012', '0947', '0원', 'ITX-마음', '여수EXPO', '용산'),
    (81, '1303', '20241012', '0953', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (82, '1303', '20241012', '0953', '0원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (83, '1401', '20241012', '1053', '47200원', 'KTX', '여수EXPO', '용산'),
    (84, '1521', '20241012', '1218', '47200원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (85, '1543', '20241012', '1245', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (86, '1710', '20241012', '1410', '47200원', 'KTX', '여수EXPO', '용산'),
    (87, '1830', '20241012', '1438', '46000원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (88, '2020', '20241012', '1504', '0원', '무궁화호', '여수EXPO', '용산'),
    (89, '2122', '20241012', '1638', '0원', 'ITX-새마을', '여수EXPO', '용산'),
    (90, '1954', '20241012', '1643', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (91, '2143', '20241012', '1718', '0원', 'ITX-마음', '여수EXPO', '용산'),
    (92, '2042', '20241012', '1744', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (93, '2151', '20241012', '1848', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (94, '0026', '20241012', '1915', '0원', '무궁화호', '여수EXPO', '용산'),
    (95, '2309', '20241012', '2008', '47200원', 'KTX', '여수EXPO', '용산'),
    (96, '2335', '20241012', '2033', '47200원', 'KTX-산천(A-type)', '여수EXPO', '용산'),
    (97, '0035', '20241012', '2148', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (98, '0810', '20241008', '0508', '47200원', 'KTX-산천(B-type)', '여수EXPO', '용산'),
    (99, '1105', '20241008', '0544', '27600원', '무궁화호', '여수EXPO', '용산'),
    (100, '1019', '20241008', '0710', '47200원', 'KTX', '여수EXPO', '용산');


-- book_mark 테이블에 더미 데이터 삽입
-- book_mark 테이블에 더미 데이터 삽입
INSERT INTO book_mark (is_sent, want_first_class, want_normal_seat, want_baby_seat, want_waiting_reservation, ticket_id, USER_ID) VALUES
                                                                                                                                      -- user1의 북마크 20개
                                                                                                                                      (false, true, 'SEAT', 'NOTFOUND', false, 1, 'user1'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 2, 'user1'),
                                                                                                                                      (false, true, 'NOTFOUND', 'STANDING_SEAT', false, 3, 'user1'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 4, 'user1'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 5, 'user1'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 6, 'user1'),
                                                                                                                                      (false, true, 'SEAT', 'SEAT', false, 7, 'user1'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'STANDING_SEAT', true, 8, 'user1'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 9, 'user1'),
                                                                                                                                      (false, false, 'SEAT', 'NOTFOUND', true, 10, 'user1'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'SEAT', false, 11, 'user1'),
                                                                                                                                      (false, false, 'NOTFOUND', 'STANDING_SEAT', true, 12, 'user1'),
                                                                                                                                      (false, true, 'SEAT', 'STANDING_SEAT', false, 13, 'user1'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 14, 'user1'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 15, 'user1'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 16, 'user1'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 17, 'user1'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 18, 'user1'),
                                                                                                                                      (false, true, 'SEAT', 'STANDING_SEAT', false, 19, 'user1'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 20, 'user1'),

                                                                                                                                      -- user2의 북마크 20개
                                                                                                                                      (false, true, 'SEAT', 'STANDING_SEAT', false, 21, 'user2'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 22, 'user2'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 23, 'user2'),
                                                                                                                                      (false, false, 'SEAT', 'SEAT', true, 24, 'user2'),
                                                                                                                                      (false, true, 'NOTFOUND', 'STANDING_SEAT', false, 25, 'user2'),
                                                                                                                                      (false, false, 'SEAT', 'NOTFOUND', true, 26, 'user2'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'SEAT', false, 27, 'user2'),
                                                                                                                                      (false, false, 'NOTFOUND', 'STANDING_SEAT', true, 28, 'user2'),
                                                                                                                                      (false, true, 'SEAT', 'NOTFOUND', false, 29, 'user2'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 30, 'user2'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 31, 'user2'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 32, 'user2'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 33, 'user2'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 34, 'user2'),
                                                                                                                                      (false, true, 'SEAT', 'STANDING_SEAT', false, 35, 'user2'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 36, 'user2'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 37, 'user2'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 38, 'user2'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 39, 'user2'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 40, 'user2'),

                                                                                                                                      -- user3의 북마크 20개
                                                                                                                                      (false, true, 'STANDING_SEAT', 'SEAT', false, 41, 'user3'),
                                                                                                                                      (false, false, 'SEAT', 'NOTFOUND', true, 42, 'user3'),
                                                                                                                                      (false, true, 'NOTFOUND', 'STANDING_SEAT', false, 43, 'user3'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 44, 'user3'),
                                                                                                                                      (false, true, 'SEAT', 'NOTFOUND', false, 45, 'user3'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 46, 'user3'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'STANDING_SEAT', false, 47, 'user3'),
                                                                                                                                      (false, false, 'SEAT', 'SEAT', true, 48, 'user3'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 49, 'user3'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 50, 'user3'),
                                                                                                                                      (false, true, 'SEAT', 'STANDING_SEAT', false, 51, 'user3'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 52, 'user3'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 53, 'user3'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 54, 'user3'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 55, 'user3'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 56, 'user3'),
                                                                                                                                      (false, true, 'SEAT', 'SEAT', false, 57, 'user3'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 58, 'user3'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 59, 'user3'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 60, 'user3'),

                                                                                                                                      -- user4의 북마크 20개
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 61, 'user4'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 62, 'user4'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 63, 'user4'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 64, 'user4'),
                                                                                                                                      (false, true, 'SEAT', 'SEAT', false, 65, 'user4'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 66, 'user4'),
                                                                                                                                      (false, true, 'NOTFOUND', 'STANDING_SEAT', false, 67, 'user4'),
                                                                                                                                      (false, false, 'SEAT', 'SEAT', true, 68, 'user4'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'SEAT', false, 69, 'user4'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 70, 'user4'),
                                                                                                                                      (false, true, 'SEAT', 'NOTFOUND', false, 71, 'user4'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 72, 'user4'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 73, 'user4'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 74, 'user4'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 75, 'user4'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 76, 'user4'),
                                                                                                                                      (false, true, 'SEAT', 'SEAT', false, 77, 'user4'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'NOTFOUND', true, 78, 'user4'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 79, 'user4'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 80, 'user4'),

                                                                                                                                      -- user5의 북마크 20개
                                                                                                                                      (false, true, 'SEAT', 'STANDING_SEAT', false, 81, 'user5'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 82, 'user5'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'SEAT', false, 83, 'user5'),
                                                                                                                                      (false, false, 'SEAT', 'NOTFOUND', true, 84, 'user5'),
                                                                                                                                      (false, true, 'NOTFOUND', 'STANDING_SEAT', false, 85, 'user5'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 86, 'user5'),
                                                                                                                                      (false, true, 'SEAT', 'SEAT', false, 87, 'user5'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 88, 'user5'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'NOTFOUND', false, 89, 'user5'),
                                                                                                                                      (false, false, 'SEAT', 'STANDING_SEAT', true, 90, 'user5'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 91, 'user5'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 92, 'user5'),
                                                                                                                                      (false, true, 'SEAT', 'NOTFOUND', false, 93, 'user5'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 94, 'user5'),
                                                                                                                                      (false, true, 'STANDING_SEAT', 'SEAT', false, 95, 'user5'),
                                                                                                                                      (false, false, 'SEAT', 'NOTFOUND', true, 96, 'user5'),
                                                                                                                                      (false, true, 'NOTFOUND', 'SEAT', false, 97, 'user5'),
                                                                                                                                      (false, false, 'STANDING_SEAT', 'SEAT', true, 98, 'user5'),
                                                                                                                                      (false, true, 'SEAT', 'SEAT', false, 99, 'user5'),
                                                                                                                                      (false, false, 'NOTFOUND', 'SEAT', true, 100, 'user5');
