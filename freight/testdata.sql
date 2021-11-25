LOCK TABLES `oomall_weight_freight` WRITE;
/*!40000 ALTER TABLE `oomall_weight_freight` DISABLE KEYS */;
INSERT INTO `oomall_weight_freight` VALUES (2,1,500,1000,100,50,10,5,0,152,1,'admin',NULL,NULL,'2021-11-17 9:18:26',NULL);
/*!40000 ALTER TABLE `oomall_weight_freight` ENABLE KEYS */;
UNLOCK TABLES;
LOCK TABLES `oomall_piece_freight` WRITE;
/*!40000 ALTER TABLE `oomall_piece_freight` DISABLE KEYS */;
INSERT INTO `oomall_piece_freight` VALUES (3,2,1,10,2,5,1,1,'admin',NULL,NULL,'2021-11-22 14:47:21',NULL);
/*!40000 ALTER TABLE `oomall_piece_freight` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `oomall_piece_freight` WRITE;
/*!40000 ALTER TABLE `oomall_piece_freight` DISABLE KEYS */;
INSERT INTO `oomall_piece_freight` VALUES (4,2,1,10,3,4,2,1,'admin',NULL,NULL,'2021-11-22 14:47:21',NULL);
/*!40000 ALTER TABLE `oomall_piece_freight` ENABLE KEYS */;
UNLOCK TABLES;
