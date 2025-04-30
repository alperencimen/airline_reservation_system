-- Insert sample users (including an admin)
INSERT INTO users (username, password, gender, age, country, is_admin, is_active) VALUES
('admin', 'admin123', 'M', 30, 'Admin', true, true),
('john_doe', 'pass123', 'M', 28, 'USA', false, true),
('jane_smith', 'pass456', 'F', 32, 'UK', false, true),
('mike_wilson', 'pass789', 'M', 45, 'Canada', false, true),
('ulas', '1', 'M', 22, 'Turkiye', false, true);

-- Insert sample flights
INSERT INTO flights (flight_number, departure_airport, arrival_airport, departure_time, arrival_time, available_seats, airline_name, is_active) VALUES
('TK1234', 'Istanbul', 'London', '2026-03-30 10:00:00', '2026-03-30 13:00:00', 150, 'Turkish Airlines', true),
('BA5678', 'London', 'New York', '2026-03-30 14:00:00', '2026-03-30 20:00:00', 210, 'British Airways', true),
('AA9012', 'New York', 'Los Angeles', '2026-03-30 09:00:00', '2026-03-30 12:00:00', 180, 'American Airlines', true),
('LH3456', 'Frankfurt', 'Tokyo', '2026-03-30 11:00:00', '2026-03-31 05:00:00', 240, 'Lufthansa', true);

-- Insert sample bookings
INSERT INTO bookings (booking_reference, user_id, flight_id, booking_date, seat_number, seat_preference, is_active) VALUES
('BK123456', 2, 1, '2026-03-28 10:00:00', '12A', 'WINDOW', true),
('BK789012', 2, 2, '2026-03-28 11:00:00', '15B', 'AISLE', true),
('BK345678', 3, 3, '2026-03-28 12:00:00', '8C', 'WINDOW', true),
('BK901234', 4, 4, '2026-03-28 13:00:00', '20B', 'AISLE', true);