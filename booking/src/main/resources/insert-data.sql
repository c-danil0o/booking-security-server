-- Insert data into the address table
INSERT INTO address (city, number, street, latitude, longitude, country)
VALUES ('City1', '123', 'Street A', 45, 29, 'Serbia'),
       ('City2', '456', 'Street B', 45, 29, 'Bosnia'),
       ('City3', '789', 'Street C', 45, 29, 'Serbia'),
       ('City4', '101', 'Street D', 45, 29, 'Serbia'),
       ('City5', '202', 'Street E', 45, 29, 'Serbia'),
       ('City6', '303', 'Street F', 45, 29, 'Serbia'),
       ('City7', '404', 'Street G', 45, 29, 'Serbia'),
       ('City8', '505', 'Street H', 45, 29, 'Serbia'),
       ('City9', '606', 'Street I', 45, 29, 'Serbia'),
       ('City10', '707', 'Street J', 45, 29, 'Serbia');

-- Insert data into the account table for hosts
INSERT INTO account (is_blocked, role, email, password, is_activated)
VALUES (false, 'Host', 'host1@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Host', 'host2@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Host', 'host3@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Host', 'host4@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),

       (false, 'Host', 'host5@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true);

-- Insert data into the host table
INSERT INTO host (address_id, id, first_name, last_name, phone)
VALUES (1, 1, 'Host', 'One', '+1234567890'),
       (2, 2, 'Host', 'Two', '+2345678901'),
       (3, 3, 'Host', 'Three', '+3456789012'),
       (4, 4, 'Host', 'Four', '+4567890123'),
       (5, 5, 'Host', 'Five', '+5678901234');

-- Insert data into the account table for guests
INSERT INTO account (is_blocked, role, email, password, is_activated)
VALUES (false, 'Guest', 'guest1@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Guest', 'guest2@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Guest', 'guest3@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Guest', 'guest4@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Guest', 'guest5@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true),
       (false, 'Admin', 'admin@example.com', '$2a$10$/UBsR5qdgZLV1WMm5FQ.h.v38vn3y0hF3gHJXKHb2dJ/.nJASvp72', true);

-- Insert data into the guest table
INSERT INTO guest (times_cancelled, address_id, id, first_name, last_name, phone)
VALUES (0, 6, 6, 'Guest', 'One', '+6789012345'),
       (1, 7, 7, 'Guest', 'Two', '+7890123456'),
       (2, 8, 8, 'Guest', 'Three', '+8901234567'),
       (3, 9, 9, 'Guest', 'Four', '+9012345678'),
       (4, 10, 10, 'Guest', 'Five', '+0123456789');


INSERT INTO account_settings(account_id, settings)
VALUES (1, 'RESERVATION_REQUEST_NOTIFICATION'),
       (1, 'RESERVATION_CANCEL_NOTIFICATION'),
       (1, 'HOST_REVIEW_NOTIFICATION'),
       (1, 'ACCOMMODATION_REVIEW_NOTIFICATION'),
       (2, 'RESERVATION_REQUEST_NOTIFICATION'),
       (2, 'RESERVATION_CANCEL_NOTIFICATION'),
       (2, 'HOST_REVIEW_NOTIFICATION'),
       (2, 'ACCOMMODATION_REVIEW_NOTIFICATION'),
       (3, 'RESERVATION_REQUEST_NOTIFICATION'),
       (3, 'RESERVATION_CANCEL_NOTIFICATION'),
       (3, 'HOST_REVIEW_NOTIFICATION'),
       (3, 'ACCOMMODATION_REVIEW_NOTIFICATION'),
       (4, 'RESERVATION_REQUEST_NOTIFICATION'),
       (4, 'RESERVATION_CANCEL_NOTIFICATION'),
       (4, 'HOST_REVIEW_NOTIFICATION'),
       (4, 'ACCOMMODATION_REVIEW_NOTIFICATION'),
       (5, 'RESERVATION_REQUEST_NOTIFICATION'),
       (5, 'RESERVATION_CANCEL_NOTIFICATION'),
       (5, 'HOST_REVIEW_NOTIFICATION'),
       (5, 'ACCOMMODATION_REVIEW_NOTIFICATION'),
       (6, 'RESERVATION_RESPONSE_NOTIFICATION'),
       (7, 'RESERVATION_RESPONSE_NOTIFICATION'),
       (8, 'RESERVATION_RESPONSE_NOTIFICATION'),
       (9, 'RESERVATION_RESPONSE_NOTIFICATION'),
       (10, 'RESERVATION_RESPONSE_NOTIFICATION');


-- Insert data into the accommodation table
INSERT INTO accommodation (average_grade, cancellation_deadline, is_auto_approval, is_price_per_guest, max_guests,
                           min_guests, address_id, id, accommodation_type, description, name, status, host_id)
VALUES (4.33, 7, true, true, 4, 1, 1, 1, 'Apartment', 'A cozy apartment', 'Cozy Apartment 1', 'Active', 1),
       (4.0, 5, true, false, 2, 1, 2, 2, 'Room', 'Comfortable room with a view', 'Room with a View', 'Active', 2),
       (4.0, 14, true, true, 6, 2, 3, 3, 'Hotel', 'Luxurious hotel with great amenities', 'Luxury Hotel', 'Pending', 3),
       (3.33, 10, false, true, 8, 4, 4, 4, 'Apartment', 'Spacious apartment for groups', 'Spacious Group Apartment',
        'Inactive', 4),
       (4.5, 3, true, false, 3, 1, 5, 5, 'Room', 'Charming room for a relaxing stay', 'Charming Room', 'Pending', 5);

-- Insert data into the accommodation_amenities table
INSERT INTO accommodation_amenities (accommodation_id, amenities)
VALUES (1, 'WiFi'),
       (1, 'TV'),
       (1, 'Kitchen'),
       (2, 'WiFi'),
       (2, 'Air Conditioning'),
       (2, 'Breakfast'),
       (3, 'Swimming Pool'),
       (3, 'Gym'),
       (3, 'Spa'),
       (4, 'WiFi'),
       (4, 'Kitchen'),
       (4, 'Parking'),
       (5, 'Air Conditioning'),
       (5, 'TV'),
       (5, 'Coffee Maker');

-- Insert data into the accommodation_availability table
INSERT INTO time_slot (is_occupied, price, start_date, end_date)
VALUES (false, 100.0, '2023-12-01', '2023-12-13'),
       (false, 110.0, '2024-01-01', '2024-01-17'),
       (false, 150.0, '2023-12-20', '2023-12-30'),
       (false, 120.0, '2024-01-15', '2024-01-27'),
       (false, 90.0, '2023-12-08', '2023-12-16'),
       (false, 95.0, '2024-01-01', '2024-01-15'),
       (false, 210.0, '2023-12-12', '2023-12-22'),
       (false, 200.0, '2024-01-18', '2024-01-28'),
       (false, 70.0, '2023-12-20', '2023-12-25'),
       (false, 75.0, '2024-01-06', '2024-01-10');

INSERT INTO accommodation_availability (accommodation_id, availability_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (3, 5),
       (3, 6),
       (4, 7),
       (4, 8),
       (5, 9),
       (5, 10);

-- Insert data into the accommodation_photos table
INSERT INTO accommodation_photos (accommodation_id, photos)
VALUES (1, 'ap1.jpg'),
       (1, 'ap2.jpg'),
       (1, 'ap3.jpg'),
       (1, 'room1_2.jpg'),
       (2, 'apart1.jpg'),
       (2, 'apart2.jpg'),
       (2, 'apart3.jpg'),
       (3, 'hotel1.jpg'),
       (3, 'hotel2.jpg'),
       (3, 'hotel3.jpg'),
       (4, 'slika1.jpg'),
       (4, 'slika2.jpg'),
       (4, 'slika3.jpg'),
       (5, 'slika11.jpg'),
       (5, 'slika21.jpg'),
       (5, 'slika31.jpg');

-- Insert data into the guest_favorites table
INSERT INTO guest_favorites (favorites_id, favorite_to_id)
VALUES (1, 6),
       (2, 7),
       (3, 8),
       (4, 9),
       (5, 10);


-- Insert data into the host_host_reviews table
/*INSERT INTO review (host_id, accommodation_id, grade, status, author_id, comment, date)
VALUES (1, null, 4, 'Approved', 6, 'Great host!', '2023-01-05 12:00:00'),
       (2, null, 5, 'Approved', 7, 'Excellent hospitality', '2023-01-05 12:00:00'),
       (3, null, 4, 'Approved', 8, 'Wonderful stay, highly recommend', '2023-01-05 12:00:00'),
       (3, null, 3, 'Approved', 9, 'Good experience overall', '2023-01-05 12:00:00'),
       (4, null, 5, 'Approved', 10, 'Perfect host, would come again', '2023-01-05 12:00:00'),
       (5, null, 4, 'Approved', 6, 'Enjoyed my stay, great host!', '2023-01-05 12:00:00'),
       (null, 1, 5, 'Approved', 7, 'Beautiful accommodation, highly recommended!', '2023-01-05 12:00:00'),
       (null, 2, 4, 'Pending', 8, 'Comfortable and clean, would come again', '2023-01-05 12:00:00'),
       (null, 3, 3, 'Approved', 9, 'Good location but room was a bit small', '2023-01-05 12:00:00'),
       (null, 3, 5, 'Approved', 10, 'Exceptional service and amenities', '2023-01-05 12:00:00'),
       (null, 4, 3, 'Approved', 6, 'Not bad, but room was noisy', '2023-01-05 12:00:00'),
       (null, 2, 4, 'Approved', 7, 'Well-maintained property with good facilities', '2023-01-05 12:00:00'),
       (null, 1, 5, 'Approved', 8, 'Superb hospitality, felt like home', '2023-01-05 12:00:00'),
       (null, 1, 3, 'Approved', 9, 'Average amenities, but good location', '2023-01-05 12:00:00'),
       (3, null, 5, 'Approved', 10, 'Absolutely amazing, 10/10', '2023-01-05 12:00:00'),
       (4, null, 4, 'Approved', 1, 'Friendly staff and comfortable stay', '2023-01-05 12:00:00'),
       (null, 5, 5, 'Approved', 2, 'Impressive accommodation with a view', '2023-01-05 12:00:00'),
       (null, 5, 4, 'Approved', 3, 'Clean and well-equipped, enjoyed my stay', '2023-01-05 12:00:00'),
       (4, null, 3, 'Approved', 4, 'Satisfactory, but could use some improvements', '2023-01-05 12:00:00'),
       (1, null, 5, 'Approved', 5, 'Incredible experience, highly satisfied', '2023-01-05 12:00:00');*/


-- Insert data into the host_properties table
-- INSERT INTO host_properties (host_id, properties_id)
-- VALUES (1, 1),
--        (2, 2),
--        (3, 3),
--        (4, 4),
--        (5, 5);

-- Insert data into the reservation table
INSERT INTO reservation (days, price, accommodation_id, guest_id, host_id, start_date, reservation_status,
                         number_of_guests, date_created)
VALUES (2, 200.0, 1, 6, 1, '2023-01-05', 'Done', 2, '2023-01-01'),
       (7, 560.0, 2, 7, 2, '2023-02-06', 'Denied', 1, '2023-01-01'),
       (10, 1500.0, 3, 8, 3, '2023-03-07', 'Done', 3, '2023-01-01'),
       (14, 1680.0, 4, 9, 4, '2023-04-08', 'Done', 4, '2023-01-01'),
       (5, 450.0, 5, 10, 5, '2023-05-01', 'Denied', 3, '2023-01-01');

/*-- Insert data into the report table
INSERT INTO report (author_id, date, reported_user_id, reason)
VALUES (6, '2023-01-01 12:00:00', 2, 'Inappropriate behavior'),
       (7, '2023-01-02 12:00:00', 4, 'Violating community guidelines'),
       (2, '2023-01-03 12:00:00', 6, 'Suspicious activity'),
       (1, '2023-01-04 12:00:00', 8, 'Harassment'),
       (2, '2023-01-05 12:00:00', 9, 'Fraudulent behavior');*/

/*-- Insert data into the notification table
INSERT INTO notification (date, receiver_id, message)
VALUES ('2023-01-01 12:00:00', 2, 'New report received'),
       ('2023-01-02 12:00:00', 4, 'Community guidelines update'),
       ('2023-01-03 12:00:00', 6, 'Account verification required'),
       ('2023-01-04 12:00:00', 8, 'Reminder: Review your recent stay'),
       ('2023-01-06 12:00:00', 1, 'System maintenance scheduled'),
       ('2023-01-07 12:00:00', 3, 'Important security update'),
       ('2023-01-08 12:00:00', 5, 'Feedback requested for your recent experience'),
       ('2023-01-09 12:00:00', 7, 'Upcoming event: Host meetup'),
       ('2023-01-10 12:00:00', 9, 'Your account status has been updated');*/