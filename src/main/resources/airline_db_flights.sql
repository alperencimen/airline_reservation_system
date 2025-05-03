-- MySQL dump 10.13  Distrib 8.0.42, for macos15 (arm64)
--
-- Host: localhost    Database: airline_db
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flights`
--bookingsflights

DROP TABLE IF EXISTS `flights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flights` (
  `id` int NOT NULL AUTO_INCREMENT,
  `flight_number` varchar(20) NOT NULL,
  `departure_airport` varchar(50) NOT NULL,
  `arrival_airport` varchar(50) NOT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL,
  `available_seats` int NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `airline_name` varchar(100) NOT NULL DEFAULT 'Unknown Airline',
  PRIMARY KEY (`id`),
  UNIQUE KEY `flight_number` (`flight_number`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flights`
--

LOCK TABLES `flights` WRITE;
/*!40000 ALTER TABLE `flights` DISABLE KEYS */;
INSERT INTO `flights` VALUES (1,'TK1234','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',149,0,'2025-05-01 16:26:06','Unknown Airline'),(2,'BA5678','London','New York','2026-03-30 14:00:00','2026-03-30 20:00:00',210,0,'2025-05-01 16:26:06','Unknown Airline'),(3,'AA9012','New York','Los Angeles','2026-03-30 09:00:00','2026-03-30 12:00:00',179,0,'2025-05-01 16:26:06','Unknown Airline'),(4,'LH3456','Frankfurt','Tokyo','2026-03-30 11:00:00','2026-03-31 05:00:00',240,0,'2025-05-01 16:26:06','Unknown Airline'),(5,'BK111111','İstanbul','Ankara','2025-10-05 12:00:00','2025-12-05 12:00:00',99,0,'2025-05-01 17:08:07','Unknown Airline'),(6,'MZ1234','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',149,0,'2025-05-01 17:20:07','Unknown Airline'),(7,'ZZ1234','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',100,0,'2025-05-01 18:25:52','Turkish Airlines'),(10,'TK1235','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',149,0,'2025-05-01 18:55:18','Turkish Airlines'),(11,'MZ1111','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',149,0,'2025-05-02 11:14:51','Turkish Airlines'),(12,'MZ2222','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',148,0,'2025-05-02 11:31:02','British Airways'),(13,'MZ3333','Istanbul','London','2026-03-30 10:00:00','2026-03-30 13:00:00',150,1,'2025-05-02 11:31:38','American Airlines');
/*!40000 ALTER TABLE `flights` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-02 23:16:03
