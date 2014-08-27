CREATE DATABASE  IF NOT EXISTS `lls` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `lls`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: lls
-- ------------------------------------------------------
-- Server version	5.5.38

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
-- Table structure for table `computepool`
--

DROP TABLE IF EXISTS `computepool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `computepool` (
  `idcomputepool` bigint(20) NOT NULL AUTO_INCREMENT,
  `compute_pool_identity` varchar(255) DEFAULT NULL,
  `computepoolname` varchar(255) NOT NULL,
  `cpuamount` int(11) NOT NULL,
  `cpurest` int(11) NOT NULL,
  `dispatchtype` varchar(255) DEFAULT NULL,
  `error` int(11) NOT NULL,
  `memoryamount` int(11) NOT NULL,
  `memoryrest` int(11) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `taskid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idcomputepool`),
  UNIQUE KEY `UK_2r4hcrbdnmeygu7lm0gki2m6c` (`computepoolname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deliverygroup`
--

DROP TABLE IF EXISTS `deliverygroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deliverygroup` (
  `idgroup` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idgroup`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `desktop`
--

DROP TABLE IF EXISTS `desktop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `desktop` (
  `idesktop` bigint(20) NOT NULL AUTO_INCREMENT,
  `poolid` bigint(20) DEFAULT NULL,
  `poolname` varchar(255) DEFAULT NULL,
  `vmid` varchar(255) DEFAULT NULL,
  `vmname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idesktop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain`
--

DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `guid` varchar(255) NOT NULL,
  `domainname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupsusers`
--

DROP TABLE IF EXISTS `groupsusers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupsusers` (
  `userid` int(11) NOT NULL,
  `groupid` int(11) NOT NULL,
  PRIMARY KEY (`userid`,`groupid`),
  KEY `FK_hb709pfbcn8qqpwy28oa69l21` (`groupid`),
  CONSTRAINT `FK_hb709pfbcn8qqpwy28oa69l21` FOREIGN KEY (`groupid`) REFERENCES `user` (`iduser`),
  CONSTRAINT `FK_ltmykw7lnwmf78df8me94n86i` FOREIGN KEY (`userid`) REFERENCES `deliverygroup` (`idgroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `host`
--

DROP TABLE IF EXISTS `host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `host` (
  `idhost` bigint(20) NOT NULL AUTO_INCREMENT,
  `hostname` varchar(255) DEFAULT NULL,
  `computepoolid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idhost`),
  KEY `FK_qb5d84d4xnoey2k79vb0kopy7` (`computepoolid`),
  CONSTRAINT `FK_qb5d84d4xnoey2k79vb0kopy7` FOREIGN KEY (`computepoolid`) REFERENCES `computepool` (`idcomputepool`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ldapconfig`
--

DROP TABLE IF EXISTS `ldapconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ldapconfig` (
  `idldap` int(11) NOT NULL AUTO_INCREMENT,
  `base` varchar(255) DEFAULT NULL,
  `domainguid` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `user_dn` varchar(255) DEFAULT NULL,
  `synctime` bigint(20) NOT NULL,
  PRIMARY KEY (`idldap`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization` (
  `idorganization` int(11) NOT NULL AUTO_INCREMENT,
  `organizationname` varchar(255) DEFAULT NULL,
  `parent` int(11) DEFAULT NULL,
  PRIMARY KEY (`idorganization`),
  KEY `FK_7ohelipraqeipyo9ft1gp11lk` (`parent`),
  CONSTRAINT `FK_7ohelipraqeipyo9ft1gp11lk` FOREIGN KEY (`parent`) REFERENCES `organization` (`idorganization`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `idrole` int(11) NOT NULL AUTO_INCREMENT,
  `authority` varchar(255) DEFAULT NULL,
  `parent` int(11) NOT NULL,
  PRIMARY KEY (`idrole`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_users`
--

DROP TABLE IF EXISTS `role_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_users` (
  `role` int(11) NOT NULL,
  `users` int(11) NOT NULL,
  PRIMARY KEY (`role`,`users`),
  KEY `FK_l3v6sw6f1cmpnlamuqqg6vefj` (`users`),
  CONSTRAINT `FK_147hsf2uflmdjl4269l5y0kf7` FOREIGN KEY (`role`) REFERENCES `role` (`idrole`),
  CONSTRAINT `FK_l3v6sw6f1cmpnlamuqqg6vefj` FOREIGN KEY (`users`) REFERENCES `user` (`iduser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session` (
  `idsession` int(11) NOT NULL AUTO_INCREMENT,
  `expire` datetime DEFAULT NULL,
  `ticket` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idsession`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `iduser` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `realname` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `domainguid` varchar(255) DEFAULT NULL,
  `organizationid` int(11) DEFAULT NULL,
  PRIMARY KEY (`iduser`),
  KEY `FK_bgjghxoeoiww7fsl2rlaed7k1` (`domainguid`),
  KEY `FK_m2wfu6al5ok6yd24hq5d2uu5j` (`organizationid`),
  CONSTRAINT `FK_bgjghxoeoiww7fsl2rlaed7k1` FOREIGN KEY (`domainguid`) REFERENCES `domain` (`guid`),
  CONSTRAINT `FK_m2wfu6al5ok6yd24hq5d2uu5j` FOREIGN KEY (`organizationid`) REFERENCES `organization` (`idorganization`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userroles`
--

DROP TABLE IF EXISTS `userroles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userroles` (
  `roleid` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  PRIMARY KEY (`roleid`,`userid`),
  KEY `FK_9fpaj94x8d921ed2n2a68nsql` (`userid`),
  CONSTRAINT `FK_9fpaj94x8d921ed2n2a68nsql` FOREIGN KEY (`userid`) REFERENCES `role` (`idrole`),
  CONSTRAINT `FK_k3t1unho700w2lg219n5xnajl` FOREIGN KEY (`roleid`) REFERENCES `user` (`iduser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-27 19:27:14
