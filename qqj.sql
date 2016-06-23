-- MySQL dump 10.13  Distrib 5.6.25, for osx10.10 (x86_64)
--
-- Host: localhost    Database: qqj
-- ------------------------------------------------------
-- Server version	5.6.25

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
-- Table structure for table `admin_permission`
--

DROP TABLE IF EXISTS `admin_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_permission`
--

LOCK TABLES `admin_permission` WRITE;
/*!40000 ALTER TABLE `admin_permission` DISABLE KEYS */;
INSERT INTO `admin_permission` VALUES (1,'角色管理','role-management'),(2,'管理业务人员','admin-list'),(3,'团队管理','team-management'),(4,'密码管理','password-management'),(5,'代理审批','customer-audit'),(6,'代理列表','customer-list');
/*!40000 ALTER TABLE `admin_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role`
--

DROP TABLE IF EXISTS `admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role`
--

LOCK TABLES `admin_role` WRITE;
/*!40000 ALTER TABLE `admin_role` DISABLE KEYS */;
INSERT INTO `admin_role` VALUES (1,'系统管理员','Administrator'),(2,'角色及权限管理','RoleAndPermission'),(3,'业务人员管理','AdminManagement'),(4,'代理管理','CustomerManagement');
/*!40000 ALTER TABLE `admin_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role_permission_xref`
--

DROP TABLE IF EXISTS `admin_role_permission_xref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_role_permission_xref` (
  `admin_role_id` bigint(20) NOT NULL,
  `admin_permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`admin_role_id`,`admin_permission_id`),
  KEY `FK_519jrrxfqdmhyyfjwm0ed7lys` (`admin_permission_id`),
  CONSTRAINT `FK_519jrrxfqdmhyyfjwm0ed7lys` FOREIGN KEY (`admin_permission_id`) REFERENCES `admin_permission` (`id`),
  CONSTRAINT `FK_5njtnihkg6xe5iubii02fw8o7` FOREIGN KEY (`admin_role_id`) REFERENCES `admin_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role_permission_xref`
--

LOCK TABLES `admin_role_permission_xref` WRITE;
/*!40000 ALTER TABLE `admin_role_permission_xref` DISABLE KEYS */;
INSERT INTO `admin_role_permission_xref` VALUES (2,1),(3,2),(4,3),(4,4),(4,5),(4,6);
/*!40000 ALTER TABLE `admin_role_permission_xref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `realname` varchar(255) NOT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lvod9bfm438ex1071ku1glb70` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1269 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES (1,'','14dae0ee804b365825e36396d23c82c5','系统管理员','11111111111','admin'),(1266,'','0b54948cff79cef5b9abedc970c7dd46','角色管理','11111111111','角色管理'),(1267,'','e4975514972c024d693695e9a9387ae0','管理业务人员','11111111111','管理业务人员'),(1268,'','6036be2fa8e06ed4bbe98762aa7a7b6d','guodong','11111111111','代理管理');
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_user_role_xref`
--

DROP TABLE IF EXISTS `admin_user_role_xref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_user_role_xref` (
  `admin_user_id` bigint(20) NOT NULL,
  `admin_role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`admin_user_id`,`admin_role_id`),
  KEY `FK_fvhh091xceynunqfj23e0nkxl` (`admin_role_id`),
  CONSTRAINT `FK_aqwjfu2sqkwsx1p5dl3rp1mw0` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_user` (`id`),
  CONSTRAINT `FK_fvhh091xceynunqfj23e0nkxl` FOREIGN KEY (`admin_role_id`) REFERENCES `admin_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user_role_xref`
--

LOCK TABLES `admin_user_role_xref` WRITE;
/*!40000 ALTER TABLE `admin_user_role_xref` DISABLE KEYS */;
INSERT INTO `admin_user_role_xref` VALUES (1,1),(1266,2),(1267,3),(1268,4);
/*!40000 ALTER TABLE `admin_user_role_xref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `certificate_number` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `founder` bit(1) NOT NULL,
  `left_code` bigint(20) DEFAULT NULL,
  `level` smallint(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `right_code` bigint(20) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `parent` bigint(20) DEFAULT NULL,
  `team` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_i9yp8r47ouc3kt0fx3uwb34kp` (`parent`),
  KEY `FK_tme6eyb78viglxppej0ivjdvl` (`team`),
  CONSTRAINT `FK_i9yp8r47ouc3kt0fx3uwb34kp` FOREIGN KEY (`parent`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_tme6eyb78viglxppej0ivjdvl` FOREIGN KEY (`team`) REFERENCES `team` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (29,'addr1','230183198603072253','2016-05-30 22:02:37','',1,0,'王国栋','35107b7c24aa0967de0df2fb78f8e444',6,1,'18514030307','18514030307',NULL,1),(30,'addr2','111111111111111111','2016-05-30 22:18:53','',1,0,'任梦楠','ed36c47689a5d81ce9ddd0bb9b2ceca9',2,1,'11111111111','11111111111',NULL,2),(31,'addr','222333111222222222','2016-05-30 22:23:33','\0',2,1,'团队1-1级代理','ef84e3b65076d080bef35b0b7c97c8de',5,1,'11111111112','11111111112',29,1),(32,'addr','222333111222222222','2016-05-30 22:28:19','\0',3,2,'团队1-2级代理','b6aab499a94f59f7214ac1afa64d09f0',4,1,'11111111113','11111111113',31,1);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_approval_customer`
--

DROP TABLE IF EXISTS `pending_approval_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pending_approval_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `certificate_number` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `level` smallint(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `parent` bigint(20) DEFAULT NULL,
  `team` bigint(20) DEFAULT NULL,
  `stage` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5i8v0ppn703mm5tu9yb52k5x8` (`parent`),
  KEY `FK_q8589exkd6nyfv2jx3tedoo0j` (`team`),
  CONSTRAINT `FK_5i8v0ppn703mm5tu9yb52k5x8` FOREIGN KEY (`parent`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_q8589exkd6nyfv2jx3tedoo0j` FOREIGN KEY (`team`) REFERENCES `team` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_approval_customer`
--

LOCK TABLES `pending_approval_customer` WRITE;
/*!40000 ALTER TABLE `pending_approval_customer` DISABLE KEYS */;
INSERT INTO `pending_approval_customer` VALUES (7,'addr','222333111222222222','2016-05-30 22:22:56',1,'团队1-1级代理','ef84e3b65076d080bef35b0b7c97c8de',0,'11111111112','11111111112',29,1,3),(8,'addr','222333111222222222','2016-05-30 22:25:23',2,'团队1-2级代理','b6aab499a94f59f7214ac1afa64d09f0',0,'11111111113','11111111113',31,1,3);
/*!40000 ALTER TABLE `pending_approval_customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pic`
--

DROP TABLE IF EXISTS `pic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `seq` smallint(6) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `open_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rfbc6xoh6f8vjb665rksyhbbx` (`open_id`),
  CONSTRAINT `FK_rfbc6xoh6f8vjb665rksyhbbx` FOREIGN KEY (`open_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pic`
--

LOCK TABLES `pic` WRITE;
/*!40000 ALTER TABLE `pic` DISABLE KEYS */;
/*!40000 ALTER TABLE `pic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `founder` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g2l9qqsoeuynt4r5ofdt1x2td` (`name`),
  KEY `FK_mw5es6l5w586efblnrryancaq` (`founder`),
  CONSTRAINT `FK_mw5es6l5w586efblnrryancaq` FOREIGN KEY (`founder`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES (1,'team1',29),(2,'team2',30);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7yyifsj7lsq4f4ussf0go54he` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weixin_pic`
--

DROP TABLE IF EXISTS `weixin_pic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weixin_pic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `qi_niu_hash` varchar(255) DEFAULT NULL,
  `type` smallint(6) DEFAULT NULL,
  `weixin_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m98udq194hcybfmjs9kbqymjc` (`weixin_user_id`),
  CONSTRAINT `FK_m98udq194hcybfmjs9kbqymjc` FOREIGN KEY (`weixin_user_id`) REFERENCES `weixin_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weixin_pic`
--

LOCK TABLES `weixin_pic` WRITE;
/*!40000 ALTER TABLE `weixin_pic` DISABLE KEYS */;
INSERT INTO `weixin_pic` VALUES (1,'2016-04-26 16:31:25','1.png',1,1),(2,'2016-04-26 16:31:34','1.png',2,1),(5,'2016-04-26 16:33:15','1.png',2,3),(6,'2016-04-26 16:33:17','1.png',1,3),(7,'2016-04-26 16:33:20','1.png',1,4),(8,'2016-04-26 16:33:25','1.png',2,4),(9,'2016-04-26 16:33:27','1.png',2,5),(10,'2016-04-26 16:33:29','1.png',1,5),(11,'2016-04-26 16:33:31','1.png',1,6),(12,'2016-04-26 16:33:34','1.png',2,6),(14,'2016-04-26 16:34:05','1.png',2,7),(15,'2016-04-26 16:34:07','1.png',1,7);
/*!40000 ALTER TABLE `weixin_pic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weixin_user`
--

DROP TABLE IF EXISTS `weixin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weixin_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `birthday` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `open_id` varchar(255) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `blog` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `height` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `wechat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_au7tgtix9jtdhniqml8fbuwro` (`open_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weixin_user`
--

LOCK TABLES `weixin_user` WRITE;
/*!40000 ALTER TABLE `weixin_user` DISABLE KEYS */;
INSERT INTO `weixin_user` VALUES (1,'1955-05-09 00:00:00','2016-04-26 16:17:10','name1','nickname1','open_id1',1,'11111111111','2016-04-26 19:46:59',NULL,NULL,NULL,NULL,NULL),(3,'1965-05-09 00:00:00','2016-04-26 16:17:32','name1','nickname1','open_id2',1,'11111111111','2016-04-26 17:54:43',NULL,NULL,NULL,NULL,NULL),(4,'1975-05-09 00:00:00','2016-04-26 16:17:42','name1','nickname1','open_id3',2,'11111111111','2016-04-26 17:55:29',NULL,NULL,NULL,NULL,NULL),(5,'1985-05-09 00:00:00','2016-04-26 16:17:55','name1','nickname1','open_id4',1,'11111111111','2016-04-26 19:47:25',NULL,NULL,NULL,NULL,NULL),(6,'1995-05-09 00:00:00','2016-04-26 16:18:18','name1','nickname1','open_id5',1,'11111111111','2016-04-26 19:47:37',NULL,NULL,NULL,NULL,NULL),(7,'2000-05-09 00:00:00','2016-04-26 16:20:47','name1','nickname1','open_id6',2,'11111111111','2016-04-26 19:47:45',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `weixin_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-30 22:35:08
