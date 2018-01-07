/*
 Navicat Premium Data Transfer

 Source Server         : localhost3307
 Source Server Type    : MySQL
 Source Server Version : 50626
 Source Host           : localhost
 Source Database       : db_aiml

 Target Server Type    : MySQL
 Target Server Version : 50626
 File Encoding         : utf-8

 Date: 01/07/2018 17:55:31 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ResponseTable`
-- ----------------------------
DROP TABLE IF EXISTS `ResponseTable`;
CREATE TABLE `ResponseTable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createTime` datetime DEFAULT NULL,
  `isDeleted` int(11) DEFAULT NULL,
  `lastModifyTime` datetime DEFAULT NULL,
  `question` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `replay` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `label` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `copyfield` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
--  Records of `ResponseTable`
-- ----------------------------
BEGIN;
INSERT INTO `ResponseTable` VALUES ('1', '2018-01-03 00:00:00', '0', '2018-01-03 00:00:00', '淘宝是什么', '淘宝是苹果', '淘宝', '淘宝 支付宝'), ('2', '2018-01-04 00:00:00', '0', '2018-01-31 00:00:00', '什么是物料', '物料是物品的组合，是对无二的<a>', '物料', '物料'), ('3', '2018-01-07 15:30:45', '0', '2018-01-07 15:30:40', '王者荣耀', '王者荣耀是一款多人在线对战游戏', null, '王者荣耀');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
