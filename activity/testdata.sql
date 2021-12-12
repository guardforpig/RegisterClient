LOCK TABLES `oomall_share_activity` WRITE;
UPDATE `oomall_share_activity` SET `shop_id` = 3, `shop_name` = '向往时刻', `name` = '分享活动2', `strategy` = '[{\"quantity\":10,\"percentage\":10},{\"quantity\":10,\"percentage\":10}]', `begin_time` = '2021-11-11 15:01:23', `end_time` = '2022-02-19 15:01:23', `state` = 2, `creator_id` = 1, `creator_name` = 'admin', `modifier_id` = NULL, `modifier_name` = NULL, `gmt_create` = '2021-11-11 15:01:23', `gmt_modified` = NULL WHERE `id` = 2;
UNLOCK TABLES;
