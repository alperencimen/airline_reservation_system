-- Drop tables if they exist (respecting foreign key dependencies)
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `flights`;
DROP TABLE IF EXISTS `users`;

-- Create users table
CREATE TABLE `users` (
                         `id` INT NOT NULL AUTO_INCREMENT,
                         `username` VARCHAR(50) NOT NULL,
                         `password` VARCHAR(255) NOT NULL,
                         `gender` VARCHAR(10) NOT NULL,
                         `age` INT NOT NULL,
                         `country` VARCHAR(50) NOT NULL,
                         `is_admin` TINYINT(1) DEFAULT 0,
                         `is_active` TINYINT(1) DEFAULT 1,
                         `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                         `default_seat_preference` VARCHAR(20) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert users
INSERT INTO `users` VALUES
                        (1,'admin','admin123','M',30,'Admin',1,1,'2025-05-01 16:26:06','AISLE'),
                        (2,'john_doe','pass123','M',28,'USA',0,1,'2025-05-01 16:26:06',NULL),
                        (3,'jane_smith','pass456','F',32,'UK',0,1,'2025-05-01 16:26:06',NULL),
                        (4,'mike_wilson','pass789','M',45,'Canada',0,1,'2025-05-01 16:26:06',NULL),
                        (5,'ulas','1','M',22,'Turkiye',0,1,'2025-05-01 16:26:06','WINDOW'),
                        (6,'melih','zekvan','Male',22,'Türkiye',0,1,'2025-05-01 17:11:26','AISLE');

-- Create flights table
CREATE TABLE `flights` (
                           `id` INT NOT NULL AUTO_INCREMENT,
                           `flight_number` VARCHAR(20) NOT NULL,
                           `departure_airport` VARCHAR(50) NOT NULL,
                           `arrival_airport` VARCHAR(50) NOT NULL,
                           `departure_time` DATETIME NOT NULL,
                           `arrival_time` DATETIME NOT NULL,
                           `available_seats` INT NOT NULL,
                           `is_active` TINYINT(1) DEFAULT 1,
                           `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                           `airline_name` VARCHAR(100) NOT NULL DEFAULT 'Unknown Airline',
                           `total_seats` INT NOT NULL DEFAULT 0,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `flight_number` (`flight_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert flights
INSERT INTO `flights` VALUES
                          (1,'TK1234','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',149,0,'2025-05-01 16:26:06','Unknown Airline',150),
                          (2,'BA5678','London','New York','2026-03-30 14:00:00','2026-03-30 20:00:00',210,0,'2025-05-01 16:26:06','Unknown Airline',210),
                          (3,'AA9012','New York','Los Angeles','2026-03-30 09:00:00','2026-03-30 12:00:00',179,0,'2025-05-01 16:26:06','Unknown Airline',179),
                          (4,'MZ3333','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',149,1,'2025-05-02 11:31:38','American Airlines',150),
                          (5,'SS2004','Madrid','İstanbul','2026-05-05 10:00:00','2026-05-05 16:30:00',99,1,'2025-05-03 14:26:12','SerdarAli',100),
                          (6,'TK9999','Berlin','Rome','2026-04-01 08:00:00','2026-04-01 10:30:00',180,1,'2025-05-04 12:00:00','Lufthansa',180),
                          (7,'AZ2020','Rome','Paris','2026-04-02 09:00:00','2026-04-02 11:00:00',170,1,'2025-05-04 12:30:00','Alitalia',170),
                          (8,'FR3003','Madrid','Lisbon','2026-04-03 10:00:00','2026-04-03 11:45:00',190,1,'2025-05-04 13:00:00','Ryanair',190),
                          (9,'TK5555','Istanbul','Berlin','2026-04-04 07:00:00','2026-04-04 09:30:00',160,1,'2025-05-04 13:30:00','Turkish Airlines',160),
                          (10,'BA7777','London','Dublin','2026-04-05 08:15:00','2026-04-05 09:45:00',175,1,'2025-05-04 14:00:00','British Airways',175),
                          (11,'DL8888','Atlanta','Miami','2026-04-06 12:00:00','2026-04-06 14:30:00',185,1,'2025-05-04 14:30:00','Delta Airlines',185),
                          (12,'AF1111','Paris','Amsterdam','2026-04-07 10:00:00','2026-04-07 11:15:00',165,1,'2025-05-04 15:00:00','Air France',165),
                          (13,'IB2003','Madrid','Istanbul','2026-04-08 11:00:00','2026-04-08 15:00:00',200,1,'2025-05-04 15:30:00','Iberia',200);

-- Create bookings table
CREATE TABLE `bookings` (
                            `id` INT NOT NULL AUTO_INCREMENT,
                            `booking_reference` VARCHAR(20) NOT NULL,
                            `user_id` INT NOT NULL,
                            `flight_id` INT NOT NULL,
                            `booking_date` DATETIME NOT NULL,
                            `seat_number` VARCHAR(10) NOT NULL,
                            `seat_preference` VARCHAR(10) NOT NULL,
                            `is_active` TINYINT(1) DEFAULT 1,
                            `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `booking_reference` (`booking_reference`),
                            CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                            CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert bookings (updated flight_ids)
INSERT INTO `bookings` VALUES
                           (1,'BK123456',2,1,'2026-03-28 10:00:00','12A','WINDOW',0,'2025-05-01 16:26:06'),
                           (2,'BK789012',2,2,'2026-03-28 11:00:00','15B','AISLE',1,'2025-05-01 16:26:06'),
                           (3,'BK345678',3,3,'2026-03-28 12:00:00','8C','WINDOW',0,'2025-05-01 16:26:06'),
                           (4,'BK901234',4,6,'2026-03-28 13:00:00','20B','AISLE',0,'2025-05-01 16:26:06'),
                           (5,'BK241420',6,1,'2025-05-01 20:14:36','25D','WINDOW',0,'2025-05-01 17:14:36'),
                           (6,'BK639680',6,1,'2025-05-01 20:17:22','24D','WINDOW',1,'2025-05-01 17:17:21'),
                           (7,'BK142907',6,8,'2025-05-01 20:20:46','23A','WINDOW',0,'2025-05-01 17:20:45'),
                           (8,'BK486970',6,1,'2025-05-01 20:33:20','22A','WINDOW',1,'2025-05-01 17:33:19'),
                           (9,'BK764859',6,7,'2025-05-01 21:46:59','1A','WINDOW',0,'2025-05-01 18:46:59'),
                           (10,'BK224516',6,3,'2025-05-01 21:48:14','1B','AISLE',0,'2025-05-01 18:48:13'),
                           (11,'BK647051',6,9,'2025-05-01 21:55:45','18C','AISLE',0,'2025-05-01 18:55:45'),
                           (12,'BK823752',6,8,'2025-05-02 10:39:32','11A','WINDOW',0,'2025-05-02 07:39:31'),
                           (13,'BK976553',6,10,'2025-05-02 14:15:43','1A','WINDOW',0,'2025-05-02 11:15:43'),
                           (14,'BK541550',6,11,'2025-05-02 14:25:19','1A','WINDOW',0,'2025-05-02 11:25:18'),
                           (15,'BK717758',5,12,'2025-05-02 14:42:27','11A','WINDOW',0,'2025-05-02 11:42:26'),
                           (16,'BK192844',6,12,'2025-05-02 14:46:56','1C','AISLE',0,'2025-05-02 11:46:55'),
                           (17,'BK761621',6,12,'2025-05-02 14:47:46','1C','AISLE',0,'2025-05-02 11:47:45');

-- Create notifications table
CREATE TABLE `notifications` (
                                 `id` INT NOT NULL AUTO_INCREMENT,
                                 `user_id` INT NOT NULL,
                                 `message` TEXT NOT NULL,
                                 `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 `is_read` TINYINT(1) DEFAULT 0,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert notifications
INSERT INTO `notifications` VALUES
                                (1,6,'Your booking \'BK823752\' was cancelled due to flight cancellation.','2025-05-02 10:40:43',0),
                                (2,6,'Your booking \'BK976553\' was cancelled due to flight cancellation.','2025-05-02 14:16:39',0),
                                (3,6,'Your booking \'BK541550\' was cancelled due to flight cancellation.','2025-05-02 14:26:30',0),
                                (4,5,'Your booking \'BK717758\' was cancelled due to flight cancellation.','2025-05-02 14:48:37',0),
                                (5,6,'Your booking \'BK761621\' was cancelled due to flight cancellation.','2025-05-02 14:48:37',0);
