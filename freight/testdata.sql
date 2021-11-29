LOCK TABLES `oomall_weight_freight` WRITE;
/*!40000 ALTER TABLE `oomall_weight_freight` DISABLE KEYS */;
INSERT IGNORE INTO `oomall_weight_freight` VALUES (2,1,500,1000,100,50,10,5,0,152,1,'admin',NULL,NULL,'2021-11-17 9:18:26',NULL);
/*!40000 ALTER TABLE `oomall_weight_freight` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `oomall_piece_freight` WRITE;
/*!40000 ALTER TABLE `oomall_piece_freight` DISABLE KEYS */;
INSERT IGNORE INTO `oomall_piece_freight` VALUES (2,2,2,500,2,100,152,1,'admin',NULL,NULL,'2021-11-17 9:18:26',NULL);
/*!40000 ALTER TABLE `oomall_piece_freight` ENABLE KEYS */;
UNLOCK TABLES;