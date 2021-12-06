-- MySQL dump 10.13  Distrib 5.7.29, for Win64 (x86_64)
--
-- Host: localhost    Database: oomall_others
-- ------------------------------------------------------
-- Server version	5.7.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `oomall_address`
--

DROP TABLE IF EXISTS `oomall_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) DEFAULT NULL,
  `region_id` bigint(20) DEFAULT NULL,
  `detail` varchar(500) DEFAULT NULL,
  `consignee` varchar(128) DEFAULT NULL,
  `mobile` varchar(128) DEFAULT NULL,
  `be_default` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_aftersale_service`
--

DROP TABLE IF EXISTS `oomall_aftersale_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_aftersale_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `order_item_id` bigint(20) DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `service_sn` varchar(128) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `conclusion` varchar(500) DEFAULT NULL,
  `price` bigint(20) DEFAULT NULL,
  `quantity` bigint(20) DEFAULT NULL,
  `region_id` bigint(20) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `consignee` varchar(128) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `customer_log_sn` varchar(128) DEFAULT NULL,
  `shop_log_sn` varchar(128) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `be_deleted` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_coupon`
--

DROP TABLE IF EXISTS `oomall_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coupon_sn` varchar(128) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `activity_id` bigint(20) DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_customer`
--

DROP TABLE IF EXISTS `oomall_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(128) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `be_deleted` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_expenditure_item`
--

DROP TABLE IF EXISTS `oomall_expenditure_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_expenditure_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `liquid_id` bigint(20) DEFAULT NULL,
  `refund_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `revenue_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `product_name` varchar(128) DEFAULT NULL,
  `account` bigint(20) DEFAULT NULL,
  `express_fee` bigint(20) DEFAULT NULL,
  `commission` bigint(20) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `sharer_id` bigint(20) DEFAULT NULL,
  `shop_revenue` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_liquidation`
--

DROP TABLE IF EXISTS `oomall_liquidation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_liquidation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` bigint(20) DEFAULT NULL,
  `shop_name` varchar(128) DEFAULT NULL,
  `liquid_date` datetime DEFAULT NULL,
  `express_fee` bigint(20) DEFAULT NULL,
  `commission` bigint(20) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `shop_revenue` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_revenue_item`
--

DROP TABLE IF EXISTS `oomall_revenue_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_revenue_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `liquid_id` bigint(20) DEFAULT NULL,
  `payment_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `orderitem_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `product_name` varchar(128) DEFAULT NULL,
  `account` bigint(20) DEFAULT NULL,
  `express_fee` bigint(20) DEFAULT NULL,
  `commission` bigint(20) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `sharer_id` bigint(20) DEFAULT NULL,
  `shop_revenue` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_share`
--

DROP TABLE IF EXISTS `oomall_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_share` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sharer_id` bigint(20) DEFAULT NULL,
  `share_act_id` bigint(20) DEFAULT NULL,
  `onsale_id` bigint(20) DEFAULT NULL,
  `quantity` bigint(20) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_shopping_cart`
--

DROP TABLE IF EXISTS `oomall_shopping_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_shopping_cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `quantity` bigint(20) DEFAULT NULL,
  `price` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_successful_share`
--

DROP TABLE IF EXISTS `oomall_successful_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_successful_share` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `share_id` bigint(20) DEFAULT NULL,
  `onsale_id` bigint(20) DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-20 15:00:26
