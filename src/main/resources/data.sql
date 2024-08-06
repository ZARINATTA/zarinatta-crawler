-- User 데이터 삽입
INSERT INTO users (user_id, phone_number) VALUES
                                              (1, '010-1234-5678'),
                                              (2, '010-2345-6789'),
                                              (3, '010-3456-7890');


-- Ticket 데이터 삽입
INSERT INTO ticket (ticket_id, ticket_type, depart_date, depart_time, depart_station, arrive_time, arrive_station, price) VALUES
                                                                                                                              (8001, '무궁화호 1209', '20240815', '20240815130400', '서울', '20240815185900', '부산', '28600원'),
                                                                                                                              (8002, 'KTX 103', '20240815', '20240815130800', '서울', '20240815162700', '부산', '53500원'),
                                                                                                                              (8003, 'KTX 033', '20240815', '20240815132500', '서울', '20240815155800', '부산', '59800원'),
                                                                                                                              (8004, 'KTX 183', '20240815', '20240815134900', '서울', '20240815162400', '부산', '59800원'),
                                                                                                                              (8005, 'KTX 1009', '20240815', '20240815135300', '서울', '20240815184500', '부산', '42600원'),
                                                                                                                              (8006, 'KTX 035', '20240815', '20240815135800', '서울', '20240815163400', '부산', '59800원'),
                                                                                                                              (8007, '무궁화호 1211', '20240815', '20240815140400', '서울', '20240815193900', '부산', '28600원'),
                                                                                                                              (8008, 'KTX 185', '20240815', '20240815141500', '서울', '20240815164300', '부산', '59800원'),
                                                                                                                              (8009, 'KTX 037', '20240815', '20240815142800', '서울', '20240815171100', '부산', '59800원'),
                                                                                                                              (8010, 'KTX 039', '20240815', '20240815145500', '서울', '20240815173800', '부산', '59800원');


INSERT INTO BOOK_MARK (bookmark_id, is_time_out, want_first_class, want_normal_seat, want_baby_seat, want_waiting_reservation, ticket_id, user_id) VALUES
                                                                                                                                                       (1, FALSE, TRUE, 'SEAT', 'STANDING_SEAT', FALSE, 8001, 1),
                                                                                                                                                       (2, FALSE, FALSE, 'STANDING_SEAT', 'SEAT', FALSE, 8001, 2),
                                                                                                                                                       (3, TRUE, TRUE, 'NOTFOUND', 'SEAT', FALSE, 8001, 3),
                                                                                                                                                       (4, FALSE, TRUE, 'SEAT', 'STANDING_SEAT', FALSE, 8002, 1),
                                                                                                                                                       (5, FALSE, FALSE, 'STANDING_SEAT', 'SEAT', FALSE, 8002, 2),
                                                                                                                                                       (6, TRUE, TRUE, 'NOTFOUND', 'SEAT', FALSE, 8002, 3),
                                                                                                                                                       (7, FALSE, TRUE, 'SEAT', 'STANDING_SEAT', FALSE, 8003, 1),
                                                                                                                                                       (8, FALSE, FALSE, 'STANDING_SEAT', 'SEAT', FALSE, 8003, 2),
                                                                                                                                                       (9, TRUE, TRUE, 'NOTFOUND', 'SEAT', FALSE, 8003, 3),
                                                                                                                                                       (10, FALSE, TRUE, 'SEAT', 'STANDING_SEAT', FALSE, 8004, 1),
                                                                                                                                                       (11, FALSE, FALSE, 'STANDING_SEAT', 'SEAT', FALSE, 8004, 2),
                                                                                                                                                       (12, TRUE, TRUE, 'NOTFOUND', 'SEAT', FALSE, 8004, 3),
                                                                                                                                                       (13, FALSE, TRUE, 'SEAT', 'STANDING_SEAT', FALSE, 8005, 1),
                                                                                                                                                       (14, FALSE, FALSE, 'STANDING_SEAT', 'SEAT', FALSE, 8005, 2),
                                                                                                                                                       (15, TRUE, TRUE, 'NOTFOUND', 'SEAT', FALSE, 8005, 3);


