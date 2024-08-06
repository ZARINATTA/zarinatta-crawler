-- User 데이터 삽입
INSERT INTO users (user_id, phone_number) VALUES
                                              (1, '010-1234-5678'),
                                              (2, '010-2345-6789'),
                                              (3, '010-3456-7890');


-- Ticket 데이터 삽입
INSERT INTO ticket (ticket_id, ticket_type, depart_date, depart_time, depart_station, arrive_time, arrive_station, price) VALUES
                                                                                                                              (8001, '무궁화호 1203', '20240805', '20240805073800', '평택', '20240805084300', '대전', '5900원'),
                                                                                                                              (8002, 'ITX-새마을 1021', '20240805', '20240805075300', '평택', '20240805084900', '대전', '8800원'),
                                                                                                                              (8003, '무궁화호 1205', '20240805', '20240805081000', '평택', '20240805091800', '대전', '5900원'),
                                                                                                                              (8004, '무궁화호 1207', '20240805', '20240805085400', '평택', '20240805102000', '대전', '5900원'),
                                                                                                                              (8005, 'ITX-마음 9905', '20240805', '20240805091400', '평택', '20240805103800', '대전', '0원'),
                                                                                                                              (8006, 'ITX-마음 1105', '20240805', '20240805091400', '평택', '20240805103800', '대전', '0원'),
                                                                                                                              (8007, 'ITX-새마을 1031', '20240805', '20240805095900', '평택', '20240805105500', '대전', '8800원'),
                                                                                                                              (8008, 'ITX-새마을 1005', '20240805', '20240805111400', '평택', '20240805121300', '대전', '8800원'),
                                                                                                                              (8009, 'ITX-마음 1107', '20240805', '20240805125400', '평택', '20240805135300', '대전', '0원'),
                                                                                                                              (8010, 'ITX-마음 9907', '20240805', '20240805125400', '평택', '20240805135300', '대전', '0원'),
                                                                                                                              (8011, 'ITX-새마을 1007', '20240805', '20240805131000', '평택', '20240805141200', '대전', '8800원'),
                                                                                                                              (8012, '무궁화호 1209', '20240805', '20240805151000', '평택', '20240805110500', '대전', '5900원'),
                                                                                                                              (8013, 'ITX-마음 1009', '20240805', '20240805144400', '평택', '20240805154000', '대전', '8800원'),
                                                                                                                              (8014, '무궁화호 1211', '20240805', '20240805101000', '평택', '20240805160700', '대전', '5900원'),
                                                                                                                              (8015, '무궁화호 1210', '20240805', '20240805140000', '평택', '20240805164500', '대전', '5900원');

INSERT INTO BOOK_MARK (bookmark_id, is_time_out, want_first_class, want_normal_seat, want_baby_seat, waiting_sold_out, ticket_id, user_id) VALUES
                                                                                                                                               (1, FALSE, 'SOLD_OUT', 'RESERVATION', 'STANDING_SEAT', FALSE, 8001, 1),
                                                                                                                                               (2, FALSE, 'RESERVATION', 'SOLD_OUT', 'STANDING_SEAT', FALSE, 8001, 2),
                                                                                                                                               (3, TRUE, 'STANDING_SEAT', 'RESERVATION', 'SOLD_OUT', FALSE, 8001, 3),
                                                                                                                                               (4, FALSE, 'SOLD_OUT', 'RESERVATION', 'STANDING_SEAT', FALSE, 8001, 1),
                                                                                                                                               (5, FALSE, 'RESERVATION', 'SOLD_OUT', 'STANDING_SEAT', FALSE, 8001, 2),
                                                                                                                                               (6, TRUE, 'STANDING_SEAT', 'RESERVATION', 'SOLD_OUT', FALSE, 8001, 3),
                                                                                                                                               (7, FALSE, 'SOLD_OUT', 'RESERVATION', 'STANDING_SEAT', FALSE, 8001, 1),
                                                                                                                                               (8, FALSE, 'RESERVATION', 'SOLD_OUT', 'STANDING_SEAT', FALSE, 8001, 2),
                                                                                                                                               (9, TRUE, 'STANDING_SEAT', 'RESERVATION', 'SOLD_OUT', FALSE, 8002, 3),
                                                                                                                                               (10, FALSE, 'SOLD_OUT', 'RESERVATION', 'STANDING_SEAT', FALSE, 8002, 1),
                                                                                                                                               (11, FALSE, 'RESERVATION', 'SOLD_OUT', 'STANDING_SEAT', FALSE, 8002, 2),
                                                                                                                                               (12, TRUE, 'STANDING_SEAT', 'RESERVATION', 'SOLD_OUT', FALSE, 8002, 3),
                                                                                                                                               (13, FALSE, 'SOLD_OUT', 'RESERVATION', 'STANDING_SEAT', FALSE, 8002, 1),
                                                                                                                                               (14, FALSE, 'RESERVATION', 'SOLD_OUT', 'STANDING_SEAT', FALSE, 8002, 2),
                                                                                                                                               (15, TRUE, 'STANDING_SEAT', 'RESERVATION', 'SOLD_OUT', FALSE, 8002, 3);


