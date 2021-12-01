DROP TABLE IF EXISTS `oomall_wechatpay_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_wechatpay_transaction`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `appid`            varchar(32)  DEFAULT NULL,
    `mchid`            varchar(32)  DEFAULT NULL,
    `out_trade_no`     varchar(32)  DEFAULT NULL,
    `transaction_id`   varchar(32)  DEFAULT NULL,
    `trade_type`       varchar(16)  DEFAULT NULL,
    `trade_state`      varchar(32)  DEFAULT NULL,
    `trade_state_desc` varchar(256) DEFAULT NULL,
    `total`            int          DEFAULT NULL,
    `payer_total`      int          DEFAULT NULL,
    `currency`         varchar(16)  DEFAULT NULL,
    `payer_currency`   varchar(16)  DEFAULT NULL,
    `openid`           varchar(128) DEFAULT NULL,
    `success_time`     datetime     DEFAULT NULL,
    `description`      varchar(127) DEFAULT NULL,
    `time_expire`      datetime     DEFAULT NULL,
    `notify_url`       varchar(256) DEFAULT NULL,
    `prepay_id`        varchar(64)  DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信支付订单';



DROP TABLE IF EXISTS `oomall_wechatpay_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_wechatpay_refund`
(
    `id`                    bigint NOT NULL AUTO_INCREMENT,
    `refund_id`             varchar(32)  DEFAULT NULL,
    `out_refund_no`         varchar(64)  DEFAULT NULL,
    `transaction_id`        varchar(32)  DEFAULT NULL,
    `out_trade_no`          varchar(32)  DEFAULT NULL,
    `channel`               varchar(16)  DEFAULT NULL,
    `user_received_account` varchar(64)  DEFAULT NULL,
    `create_time`           datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                varchar(32)  DEFAULT NULL,
    `total`                 int          DEFAULT NULL,
    `refund`                int          DEFAULT NULL,
    `payer_total`           int          DEFAULT NULL,
    `payer_refund`          int          DEFAULT NULL,
    `settlement_refund`     int          DEFAULT NULL,
    `settlement_total`      int          DEFAULT NULL,
    `discount_refund`       int          DEFAULT NULL,
    `currency`              varchar(16)  DEFAULT NULL,
    `reason`                varchar(80)  DEFAULT NULL,
    `notify_url`            varchar(256) DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信支付退款单';