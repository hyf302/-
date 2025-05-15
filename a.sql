-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: scenic_spot_reservation
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` int DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (3,'admin','$2a$10$FhjZjXwhzqC0bFx1SHE84ujhPPlQ5Q/5Zdu5LcjFrXj6eCYO3hxjm',NULL,NULL,NULL,1,'2025-02-20 14:48:18','2025-04-03 23:35:09');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-未发布，1-已发布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (2,'1','111',1,'2025-05-15 03:24:49','2025-05-15 03:24:49'),(3,'2','222',1,'2025-05-15 05:14:33','2025-05-15 05:28:15'),(4,'1','1',1,'2025-05-15 06:03:31','2025-05-15 06:03:31');
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_level`
--

DROP TABLE IF EXISTS `member_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_level` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '等级名称',
  `min_points` int NOT NULL COMMENT '最小积分',
  `max_points` int NOT NULL COMMENT '最大积分',
  `discount` decimal(3,2) NOT NULL COMMENT '折扣率',
  `privileges` text COMMENT '特权描述',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员等级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_level`
--

LOCK TABLES `member_level` WRITE;
/*!40000 ALTER TABLE `member_level` DISABLE KEYS */;
INSERT INTO `member_level` VALUES (1,'普通会员',0,999,1.00,'基础预约服务',1,'2025-02-04 19:02:47','2025-02-04 19:02:47'),(2,'银卡会员',1000,4999,0.95,'95折优惠\n优先预约服务',1,'2025-02-04 19:02:47','2025-02-04 19:02:47'),(3,'金卡会员',5000,9999,0.90,'9折优惠\n优先预约服务\n特殊活动优先参与',1,'2025-02-04 19:02:47','2025-02-04 19:02:47'),(4,'钻石会员',10000,999999,0.85,'85折优惠\n最优先预约服务\n特殊活动优先参与\nVIP专属客服',1,'2025-02-04 19:02:47','2025-02-04 19:02:47');
/*!40000 ALTER TABLE `member_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_record`
--

DROP TABLE IF EXISTS `points_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `points` int NOT NULL,
  `type` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `related_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `points_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='积分记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_record`
--

LOCK TABLES `points_record` WRITE;
/*!40000 ALTER TABLE `points_record` DISABLE KEYS */;
INSERT INTO `points_record` VALUES (1,16,200,'系统迁移','预约成功奖励：预约ID-50',NULL,'2025-02-04 19:04:05'),(2,16,800,'系统迁移','预约成功奖励：预约ID-51',NULL,'2025-02-04 19:04:21'),(3,16,190,'系统迁移','预约成功奖励：预约ID-52',NULL,'2025-02-04 19:04:33'),(4,16,-100,'系统迁移','取消预约扣除：预约ID-50',NULL,'2025-02-04 19:08:33'),(5,16,-60,'系统迁移','取消预约扣除：预约ID-49',NULL,'2025-02-04 19:08:43'),(6,16,190,'系统迁移','预约成功奖励：预约ID-53',NULL,'2025-02-04 19:08:58'),(7,16,114,'系统迁移','预约成功奖励：预约ID-54',NULL,'2025-02-05 14:15:50'),(8,16,-400,'系统迁移','取消预约扣除：预约ID-51',NULL,'2025-02-05 14:16:14'),(9,16,200,'系统迁移','预约成功奖励：预约ID-55',NULL,'2025-02-05 14:17:13'),(10,16,190,'系统迁移','预约成功奖励：预约ID-56',NULL,'2025-02-06 18:05:10'),(11,16,-95,'系统迁移','取消预约扣除：预约ID-56',NULL,'2025-02-06 18:05:22'),(12,16,-100,'系统迁移','取消预约扣除：预约ID-55',NULL,'2025-02-06 18:05:33'),(13,16,-57,'系统迁移','取消预约扣除：预约ID-54',NULL,'2025-02-06 18:18:19'),(14,16,-95,'系统迁移','取消预约扣除：预约ID-53',NULL,'2025-02-06 18:18:22'),(15,16,200,'系统迁移','预约成功奖励：预约ID-57',NULL,'2025-02-06 18:18:37'),(16,16,190,'系统迁移','预约成功奖励：预约ID-58',NULL,'2025-02-13 15:27:24'),(17,16,190,'系统迁移','预约成功奖励：预约ID-59',NULL,'2025-02-13 15:31:05'),(18,16,190,'系统迁移','预约成功奖励：预约ID-60',NULL,'2025-02-13 15:36:07'),(19,16,-95,'系统迁移','取消预约扣除：预约ID-59',NULL,'2025-02-13 15:43:07'),(20,16,-95,'系统迁移','取消预约扣除：预约ID-60',NULL,'2025-02-13 15:43:17'),(21,16,-95,'系统迁移','取消预约扣除：预约ID-58',NULL,'2025-02-13 15:43:24'),(22,16,-100,'系统迁移','取消预约扣除：预约ID-57',NULL,'2025-02-13 15:43:32'),(23,16,-95,'系统迁移','取消预约扣除：预约ID-52',NULL,'2025-02-13 15:43:35'),(24,16,190,'系统迁移','预约成功奖励：预约ID-61',NULL,'2025-02-13 15:52:19'),(25,16,190,'系统迁移','预约成功奖励：预约ID-62',NULL,'2025-02-13 15:53:35'),(26,16,-95,'系统迁移','取消预约扣除：预约ID-61',NULL,'2025-02-13 15:53:43'),(27,16,114,'系统迁移','预约成功奖励：预约ID-63',NULL,'2025-02-13 15:57:47'),(28,16,-95,'系统迁移','取消预约扣除：预约ID-62',NULL,'2025-02-13 16:00:17'),(29,16,-114,'系统迁移','取消预约扣除：预约ID-63',NULL,'2025-02-13 16:02:49'),(30,16,190,'系统迁移','预约成功奖励：预约ID-64',NULL,'2025-02-13 18:23:27'),(31,16,190,'系统迁移','预约成功奖励：预约ID-64',NULL,'2025-02-13 18:23:32'),(32,16,190,'系统迁移','预约成功奖励：预约ID-65',NULL,'2025-02-13 18:23:52'),(33,16,190,'系统迁移','预约成功奖励：预约ID-65',NULL,'2025-02-13 18:24:01'),(34,16,190,'系统迁移','预约成功奖励：预约ID-66',NULL,'2025-02-14 18:35:12'),(35,16,190,'系统迁移','预约成功奖励：预约ID-66',NULL,'2025-02-14 18:35:21'),(36,16,190,'系统迁移','预约成功奖励：预约ID-67',NULL,'2025-02-14 18:35:38'),(37,16,-190,'系统迁移','取消预约扣除：预约ID-67',NULL,'2025-02-14 18:35:47'),(38,16,190,'系统迁移','预约成功奖励：预约ID-68',NULL,'2025-02-14 22:38:08'),(39,16,-190,'系统迁移','预约退款扣除积分：预约ID-66',NULL,'2025-02-14 22:38:43'),(40,16,190,'系统迁移','预约支付成功奖励：预约ID-68',NULL,'2025-02-14 23:38:16'),(41,16,-190,'系统迁移','预约退款扣除积分：预约ID-68',NULL,'2025-02-14 23:38:25'),(64,16,190,'预约支付','预约支付成功奖励：预约ID-70',NULL,'2025-02-16 16:19:27'),(65,16,-190,'预约支付','预约退款扣除积分：预约ID-70',NULL,'2025-02-16 16:19:35'),(66,16,-190,'预约支付','预约退款扣除积分：预约ID-64',NULL,'2025-02-17 18:32:04'),(67,16,190,'预约支付','预约支付成功奖励：预约ID-71',NULL,'2025-02-17 18:33:10'),(68,16,10,'预约支付','评价景点奖励积分：预约ID-65',NULL,'2025-02-17 19:08:27'),(69,16,190,'预约支付','预约支付成功奖励：预约ID-72',NULL,'2025-02-17 19:42:00'),(70,16,10,'预约支付','评价景点奖励积分：预约ID-72',NULL,'2025-02-18 16:27:55'),(71,16,10,'预约支付','评价景点奖励积分：预约ID-71',NULL,'2025-03-02 21:50:03'),(72,16,10,'预约支付','评价景点奖励积分：预约ID-75',NULL,'2025-04-02 02:34:15'),(73,16,190,'预约支付','预约支付成功奖励：预约ID-77',NULL,'2025-04-08 20:41:13'),(74,16,380,'预约支付','预约支付成功奖励：预约ID-78',NULL,'2025-04-08 21:34:35'),(75,16,190,'预约支付','预约支付成功奖励：预约ID-79',NULL,'2025-04-08 22:27:49'),(76,16,10,'预约支付','评价景点奖励积分：预约ID-83',NULL,'2025-05-07 22:38:38'),(77,72,200,'PAYMENT','预约支付获得积分',NULL,'2025-05-07 23:56:30'),(78,16,95,'PAYMENT','预约支付获得积分',NULL,'2025-05-07 23:57:17'),(79,72,10,'预约支付','评价景点奖励积分：预约ID-88',NULL,'2025-05-08 02:00:15'),(80,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-94',NULL,'2025-05-08 02:31:44'),(81,73,100,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-96',NULL,'2025-05-13 12:57:56'),(82,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-97',NULL,'2025-05-13 13:22:58'),(83,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-98',NULL,'2025-05-13 13:30:47'),(84,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-100',NULL,'2025-05-13 16:44:05'),(85,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-101',NULL,'2025-05-13 16:45:34'),(86,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-110',NULL,'2025-05-15 02:57:07'),(87,16,95,'RESERVATION_PAYMENT','预约支付成功奖励：预约ID-116',NULL,'2025-05-15 03:05:13');
/*!40000 ALTER TABLE `points_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scenic_spot_id` bigint NOT NULL COMMENT '景点ID',
  `reservation_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(20) DEFAULT NULL COMMENT '时间段',
  `visitor_count` int DEFAULT '1' COMMENT '访问人数',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `actual_price` decimal(10,2) DEFAULT NULL COMMENT '实付金额',
  `status` tinyint DEFAULT '0' COMMENT '状态(0-待支付 1-已支付 2-已取消 3-已完成)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_scenic_spot_id` (`scenic_spot_id`),
  KEY `idx_reservation_date` (`reservation_date`),
  KEY `idx_spot_date` (`scenic_spot_id`,`reservation_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,10,12,'2025-02-05','afternoon',2,300.00,300.00,0,'2025-02-02 15:48:02','2025-02-02 15:48:02'),(2,11,15,'2025-02-14','afternoon',1,150.00,150.00,0,'2025-02-02 15:53:23','2025-02-02 15:53:23'),(3,11,14,'2025-02-20','morning',1,120.00,120.00,0,'2025-02-02 15:57:53','2025-02-02 15:57:53'),(4,11,19,'2025-02-13','morning',1,80.00,80.00,0,'2025-02-02 16:01:04','2025-02-02 16:01:04'),(5,11,21,'2025-02-14','morning',1,150.00,150.00,0,'2025-02-02 16:01:17','2025-02-02 16:01:17'),(6,11,25,'2025-02-13','morning',1,80.00,80.00,0,'2025-02-02 16:04:21','2025-02-02 16:04:21'),(7,11,27,'2025-02-13','morning',1,150.00,150.00,0,'2025-02-02 16:04:36','2025-02-02 16:04:36'),(64,16,354,'2025-02-22','下午',1,200.00,190.00,4,'2025-02-13 18:23:27','2025-02-17 18:32:04'),(65,16,354,'2025-02-15','上午',1,200.00,190.00,2,'2025-02-13 18:23:52','2025-03-03 00:26:50'),(66,16,354,'2025-02-22','下午',1,200.00,190.00,4,'2025-02-14 18:35:12','2025-02-14 22:38:43'),(68,16,354,'2025-02-19','上午',1,200.00,190.00,4,'2025-02-14 22:38:08','2025-02-14 23:38:25'),(70,16,354,'2025-02-21','上午',1,200.00,190.00,4,'2025-02-16 16:17:35','2025-02-16 16:19:35'),(71,16,354,'2025-02-28','上午',1,200.00,190.00,2,'2025-02-17 18:32:36','2025-03-03 00:26:50'),(72,16,354,'2025-02-18','上午',1,200.00,190.00,2,'2025-02-17 19:02:19','2025-03-03 00:26:50'),(75,16,354,'2025-03-04','MORNING',1,200.00,190.00,2,'2025-03-03 22:47:43','2025-03-23 22:48:59'),(77,16,354,'2025-04-12','MORNING',1,200.00,190.00,2,'2025-04-02 02:33:52','2025-04-12 01:33:51'),(78,16,354,'2025-04-09','MORNING',2,400.00,380.00,2,'2025-04-08 21:34:30','2025-04-09 15:34:18'),(79,16,354,'2025-04-10','MORNING',1,200.00,190.00,2,'2025-04-08 22:27:42','2025-04-10 01:57:37'),(80,16,354,'2025-05-07','MORNING',1,200.00,190.00,3,'2025-05-06 00:48:34','2025-05-06 00:49:30'),(81,16,354,'2025-05-16','MORNING',1,200.00,190.00,2,'2025-05-06 00:56:05','2025-05-06 00:56:31'),(82,16,354,'2025-05-17','MORNING',1,200.00,190.00,1,'2025-05-06 00:56:54','2025-05-06 00:56:58'),(83,16,354,'2025-05-17','MORNING',1,200.00,190.00,2,'2025-05-06 00:57:09','2025-05-06 00:57:14'),(84,11,350,'2025-05-10','MORNING',1,120.00,120.00,4,'2025-05-07 22:43:22','2025-05-10 00:00:01'),(85,16,354,'2025-05-08','MORNING',1,200.00,190.00,2,'2025-05-07 22:44:01','2025-05-08 00:00:00'),(86,72,354,'2025-05-08','MORNING',1,200.00,200.00,2,'2025-05-07 23:44:34','2025-05-08 00:00:00'),(87,72,354,'2025-05-08','MORNING',1,200.00,200.00,2,'2025-05-07 23:47:30','2025-05-08 00:00:00'),(88,72,354,'2025-05-08','MORNING',1,200.00,200.00,2,'2025-05-07 23:56:23','2025-05-08 00:00:00'),(89,16,356,'2025-05-09','MORNING',1,100.00,95.00,4,'2025-05-07 23:57:12','2025-05-09 00:00:00'),(90,72,356,'2025-05-09','MORNING',1,100.00,100.00,4,'2025-05-08 01:59:40','2025-05-09 00:00:00'),(91,72,350,'2025-05-09','MORNING',1,120.00,120.00,4,'2025-05-08 02:00:42','2025-05-09 00:00:00'),(92,72,354,'2025-05-09','MORNING',1,200.00,200.00,4,'2025-05-08 02:00:57','2025-05-09 00:00:00'),(93,16,356,'2025-05-09','MORNING',1,100.00,95.00,4,'2025-05-08 02:14:35','2025-05-09 00:00:00'),(94,16,356,'2025-05-09','MORNING',1,100.00,95.00,4,'2025-05-08 02:31:38','2025-05-09 00:00:00'),(95,72,356,'2025-05-08','MORNING',1,100.00,100.00,2,'2025-05-08 03:08:36','2025-05-13 16:31:28'),(96,73,352,'2025-05-15','MORNING',1,100.00,100.00,1,'2025-05-13 12:57:12','2025-05-13 12:57:55'),(97,16,356,'2025-05-14','MORNING',1,100.00,95.00,4,'2025-05-13 13:22:53','2025-05-14 01:43:43'),(98,16,356,'2025-05-13','AFTERNOON',1,100.00,95.00,4,'2025-05-13 13:30:37','2025-05-14 01:43:43'),(99,72,349,'2025-05-13','MORNING',1,80.00,80.00,2,'2025-05-13 16:31:58','2025-05-13 17:25:44'),(100,16,356,'2025-05-14','MORNING',1,100.00,95.00,4,'2025-05-13 16:43:47','2025-05-14 01:43:43'),(101,16,356,'2025-05-13','MORNING',1,100.00,95.00,4,'2025-05-13 16:45:21','2025-05-14 01:43:43'),(102,72,356,'2025-05-15','MORNING',1,100.00,100.00,2,'2025-05-13 17:26:26','2025-05-13 17:26:30'),(103,16,356,'2025-05-13','MORNING',1,100.00,95.00,2,'2025-05-13 17:26:59','2025-05-13 17:27:04'),(104,16,356,'2025-05-15','MORNING',1,100.00,95.00,2,'2025-05-13 17:27:54','2025-05-13 17:27:59'),(105,16,356,'2025-05-13','AFTERNOON',1,100.00,95.00,3,'2025-05-13 17:34:01','2025-05-13 17:34:05'),(106,16,356,'2025-05-13','MORNING',1,100.00,95.00,3,'2025-05-13 17:35:29','2025-05-13 17:35:39'),(107,16,356,'2025-05-14','MORNING',1,100.00,95.00,3,'2025-05-14 04:01:12','2025-05-14 04:24:27'),(108,16,356,'2025-05-14','MORNING',1,100.00,95.00,3,'2025-05-14 04:44:34','2025-05-14 04:45:08'),(109,16,356,'2025-05-14','MORNING',1,100.00,95.00,3,'2025-05-14 04:45:25','2025-05-15 00:34:33'),(110,16,356,'2025-05-16','MORNING',1,100.00,95.00,1,'2025-05-15 02:57:02','2025-05-15 02:57:07'),(111,16,356,'2025-05-15','MORNING',1,100.00,95.00,3,'2025-05-15 02:57:30','2025-05-15 02:57:40'),(112,16,356,'2025-05-15','AFTERNOON',1,100.00,95.00,3,'2025-05-15 02:58:13','2025-05-15 02:58:18'),(113,16,356,'2025-05-15','MORNING',1,100.00,95.00,3,'2025-05-15 03:00:45','2025-05-15 03:00:50'),(114,16,356,'2025-05-15','MORNING',1,100.00,95.00,3,'2025-05-15 03:01:07','2025-05-15 03:01:50'),(115,16,356,'2025-05-15','MORNING',1,100.00,95.00,3,'2025-05-15 03:04:37','2025-05-15 03:04:54'),(116,16,356,'2025-05-15','MORNING',1,100.00,95.00,1,'2025-05-15 03:05:09','2025-05-15 03:05:13');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `reservation_id` bigint NOT NULL,
  `scenic_spot_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `content` text,
  `images` varchar(1000) DEFAULT NULL,
  `status` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `reservation_id` (`reservation_id`),
  KEY `scenic_spot_id` (`scenic_spot_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`),
  CONSTRAINT `review_ibfk_3` FOREIGN KEY (`scenic_spot_id`) REFERENCES `scenic_spot` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,16,65,354,5,'山水秀美',NULL,1,'2025-02-17 19:08:28','2025-02-17 19:08:28'),(2,16,72,354,3,'中规中矩',NULL,1,NULL,NULL),(3,16,71,354,1,'很差',NULL,1,NULL,NULL),(4,16,75,354,5,'good',NULL,1,NULL,NULL),(5,16,83,354,5,'很好',NULL,1,NULL,NULL),(6,72,88,354,5,'很好',NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scenic_spot`
--

DROP TABLE IF EXISTS `scenic_spot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_spot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name` varchar(100) NOT NULL COMMENT '景点名称',
  `description` text COMMENT '景点描述',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `open_time` varchar(100) DEFAULT NULL COMMENT '开放时间',
  `close_time` varchar(100) DEFAULT NULL COMMENT '关闭时间',
  `max_capacity` int DEFAULT NULL COMMENT '每日最大接待量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '门票价格',
  `image_urls` text COMMENT '图片URL列表(JSON)',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-关闭 1-开放)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=357 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_spot`
--

LOCK TABLES `scenic_spot` WRITE;
/*!40000 ALTER TABLE `scenic_spot` DISABLE KEYS */;
INSERT INTO `scenic_spot` VALUES (349,'西湖','西湖风景秀丽，是杭州市著名景点','浙江省杭州市','08:00','17:00',10000,80.00,'',0,'2025-02-04 14:14:42','2025-05-15 06:50:46'),(350,'故宫','中国明清两代的皇家宫殿','北京市东城区','08:30','17:00',8000,120.00,NULL,0,'2025-02-04 14:14:42','2025-05-15 06:50:46'),(351,'黄山','黄山风景区，山清水秀','安徽省黄山市','06:00','18:00',5000,150.00,NULL,1,'2025-02-04 14:14:42','2025-05-15 06:50:46'),(352,'长城','万里长城，中华文明的象征','北京市八达岭','07:30','17:30',15000,100.00,NULL,0,'2025-02-04 14:14:42','2025-05-15 06:50:46'),(353,'兵马俑','秦始皇陵兵马俑博物馆','西安市临潼区','08:30','18:00',6000,120.00,NULL,0,'2025-02-04 14:14:42','2025-05-15 06:50:46'),(354,'桂林山水','漓江山水甲天下','广西桂林市','00:00','24:00',20000,200.00,NULL,1,'2025-02-04 14:14:42','2025-02-04 14:14:42'),(356,'洪崖洞','重庆著名景点','重庆市','08:00','23:00',50000,100.00,'/uploads/2025/05/07/1746615301517_3aa07f4b.jpg',0,'2025-05-06 05:51:43','2025-05-15 06:50:46');
/*!40000 ALTER TABLE `scenic_spot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT NULL COMMENT '性别(0-未知 1-男 2-女)',
  `member_level` tinyint DEFAULT '0' COMMENT '会员等级(0-普通 1-白银 2-黄金 3-黑钻)',
  `points` int DEFAULT '0' COMMENT '会员积分',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用 1-正常)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (3,'user1','$2a$10$omSKIKA5ma2q.Qs7motcQ.ICI7TEzc1H7U0.qg4hcHHkf5zfsE2OG','11111@qq.com','11111111',NULL,NULL,NULL,0,0,1,NULL,'2025-01-22 18:03:08','2025-05-13 16:07:42'),(4,'user2','$2a$10$dTraPATRa/NRZ4bxF6R6DeTMj2pSiPjae3EeLR0E4wwR48C0CbFPe','222222@qq.com','2222222',NULL,NULL,NULL,0,0,1,NULL,'2025-01-22 18:05:34','2025-01-22 18:05:34'),(5,'user3','$2a$10$tVBlKe6k3MbbtIuKt4VV.OKwC6FkE1zxfA/rG8rpBbCoFQ1SwfgPG','3333333@qq.com','33333333',NULL,NULL,NULL,0,0,1,'2025-02-02 15:48:27','2025-01-22 18:08:26','2025-02-02 15:48:27'),(16,'user5','$2a$10$QRa21qQR2JA/AXTqGoI/fumAiCGCAgqaeF72qHVApFD5JH/BBYYr6','55555@qq.com','55555',NULL,NULL,NULL,0,4357,1,'2025-04-08 20:00:46','2025-02-02 16:10:28','2025-05-07 22:41:10'),(69,'test','$2a$10$iHz0ktgeA1utzbXs4904heve0S8mn3K5Ogpkai7VXVFaxnpSRvrD.','test@example.com','13800138000',NULL,NULL,NULL,0,0,1,NULL,'2025-02-04 14:14:42','2025-02-04 14:14:42'),(70,'user6','$2a$10$DVpr3QRKftajs/HYZQiyVu1UeOWY1zZrxfpJdeKC5sm37H/y3MOVK','66666@qq.com','666666666',NULL,NULL,NULL,0,0,1,NULL,'2025-05-07 22:41:46','2025-05-07 23:30:13'),(71,'user7','$2a$10$EpwQkJal6WcjnbJ409Ua0u2xwgB/EB3WgaT1ieCEQucawQ8SysYxO','777777@qq.com','7777777',NULL,NULL,NULL,0,0,1,NULL,'2025-05-07 22:47:11','2025-05-07 23:30:13'),(72,'user8','$2a$10$Bkph3itdpWWmPsGAJRmrr.SRGyjRx6OOiXWFCRn9tsKRt2mWOJqpu','88888@qq.com','8888888',NULL,NULL,NULL,0,210,1,NULL,'2025-05-07 23:44:14','2025-05-13 13:31:16'),(73,'user9','$2a$10$PaXJnyXfHClbV0zZOaMOju7eOj1vQzsF9cU1BkY1e0epPrKGMkXEq','9999999@qq.com','99999999',NULL,NULL,NULL,0,100,1,NULL,'2025-05-13 12:55:38','2025-05-13 12:55:38');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-16  3:37:03
