-- ============================================
-- UserManagement — 数据库初始化脚本
-- 来源: 教师 hdpro.sql (29张表) + 外键约束 + 测试账号
-- ============================================

CREATE DATABASE IF NOT EXISTS user_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE user_management;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 系统用户
-- ============================================

DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工id',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(BCrypt)',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `type` tinyint DEFAULT NULL COMMENT '用户类型(0:GENERAL 1:ADMIN)',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号码',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `additional_info` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `post_id` bigint DEFAULT NULL COMMENT '岗位id',
  `status` tinyint DEFAULT 1 COMMENT '账号状态(1:正常 0:禁用)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `code` varchar(64) NOT NULL COMMENT '岗位编码',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '岗位名称',
  `description` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态(1:正常 0:停用)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记(0:可用 1:已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

-- ============================================
-- 2. C端用户
-- ============================================

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号码(用做登录用户名)',
  `password` varchar(100) DEFAULT NULL COMMENT '密码(BCrypt)',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像url',
  `nickname` varchar(20) DEFAULT NULL COMMENT '昵称',
  `status` tinyint DEFAULT 1 COMMENT '账号状态',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表(租客/房东)';

-- ============================================
-- 3. 省市区三级
-- ============================================

DROP TABLE IF EXISTS `province_info`;
CREATE TABLE `province_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '省份id',
  `name` varchar(16) DEFAULT NULL COMMENT '省份名称',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `city_info`;
CREATE TABLE `city_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '城市id',
  `name` varchar(16) DEFAULT NULL COMMENT '城市名称',
  `province_id` int DEFAULT NULL COMMENT '所属省份id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `district_info`;
CREATE TABLE `district_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '区域id',
  `name` varchar(255) DEFAULT NULL COMMENT '区域名称',
  `city_id` int DEFAULT NULL COMMENT '所属城市id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. 公寓 & 房间
-- ============================================

DROP TABLE IF EXISTS `apartment_info`;
CREATE TABLE `apartment_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公寓id',
  `name` varchar(64) DEFAULT NULL COMMENT '公寓名称',
  `introduction` varchar(255) DEFAULT NULL COMMENT '公寓介绍',
  `district_id` bigint DEFAULT NULL COMMENT '所处区域id',
  `district_name` varchar(16) DEFAULT NULL COMMENT '区域名称',
  `city_id` bigint DEFAULT NULL COMMENT '所处城市id',
  `city_name` varchar(16) DEFAULT NULL COMMENT '城市名称',
  `province_id` bigint DEFAULT NULL COMMENT '所处省份id',
  `province_name` varchar(16) DEFAULT NULL COMMENT '省份名称',
  `address_detail` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `latitude` varchar(16) DEFAULT NULL COMMENT '经度',
  `longitude` varchar(16) DEFAULT NULL COMMENT '纬度',
  `phone` varchar(11) DEFAULT NULL COMMENT '公寓前台电话',
  `is_release` tinyint DEFAULT NULL COMMENT '是否发布(1:发布 0:未发布)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公寓信息表';

DROP TABLE IF EXISTS `room_info`;
CREATE TABLE `room_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房间id',
  `room_number` varchar(16) DEFAULT NULL COMMENT '房间号',
  `rent` decimal(16,2) DEFAULT NULL COMMENT '租金(元/月)',
  `apartment_id` bigint DEFAULT NULL COMMENT '所属公寓id',
  `is_release` tinyint DEFAULT NULL COMMENT '是否发布',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间信息表';

-- ============================================
-- 5. 字典表 (6组)
-- ============================================

DROP TABLE IF EXISTS `facility_info`;
CREATE TABLE `facility_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `type` tinyint DEFAULT NULL COMMENT '类型(1:公寓配套 2:房间配套)',
  `name` varchar(16) DEFAULT NULL COMMENT '名称',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配套信息表';

DROP TABLE IF EXISTS `label_info`;
CREATE TABLE `label_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint DEFAULT NULL COMMENT '类型(1:公寓标签 2:房间标签)',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名称',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签信息表';

DROP TABLE IF EXISTS `attr_key`;
CREATE TABLE `attr_key` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(16) DEFAULT NULL COMMENT '属性key',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间基本属性表';

DROP TABLE IF EXISTS `attr_value`;
CREATE TABLE `attr_value` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(16) DEFAULT NULL COMMENT '属性value',
  `attr_key_id` bigint DEFAULT NULL COMMENT '对应的属性key_id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间基本属性值表';

DROP TABLE IF EXISTS `fee_key`;
CREATE TABLE `fee_key` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(16) DEFAULT NULL COMMENT '付款项key',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='杂项费用名称表';

DROP TABLE IF EXISTS `fee_value`;
CREATE TABLE `fee_value` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '费用value',
  `unit` varchar(255) DEFAULT NULL COMMENT '收费单位',
  `fee_key_id` bigint DEFAULT NULL COMMENT '费用对应的fee_key',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='杂项费用值表';

DROP TABLE IF EXISTS `lease_term`;
CREATE TABLE `lease_term` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `month_count` int DEFAULT NULL COMMENT '租期(月数)',
  `unit` varchar(16) DEFAULT NULL COMMENT '租期单位',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租期';

DROP TABLE IF EXISTS `payment_type`;
CREATE TABLE `payment_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(16) DEFAULT NULL COMMENT '付款方式名称',
  `pay_month_count` int DEFAULT NULL COMMENT '每次支付租期数',
  `additional_info` varchar(255) DEFAULT NULL COMMENT '付费说明',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式表';

-- ============================================
-- 6. 公寓关联表
-- ============================================

DROP TABLE IF EXISTS `apartment_facility`;
CREATE TABLE `apartment_facility` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint DEFAULT NULL COMMENT '公寓id',
  `facility_id` bigint DEFAULT NULL COMMENT '设施id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公寓&配套关联表';

DROP TABLE IF EXISTS `apartment_fee_value`;
CREATE TABLE `apartment_fee_value` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint DEFAULT NULL COMMENT '公寓id',
  `fee_value_id` bigint DEFAULT NULL COMMENT '收费项value_id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公寓&杂费关联表';

DROP TABLE IF EXISTS `apartment_label`;
CREATE TABLE `apartment_label` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint DEFAULT NULL COMMENT '公寓id',
  `label_id` bigint DEFAULT NULL COMMENT '标签id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公寓标签关联表';

-- ============================================
-- 7. 房间关联表
-- ============================================

DROP TABLE IF EXISTS `room_facility`;
CREATE TABLE `room_facility` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint DEFAULT NULL COMMENT '房间id',
  `facility_id` bigint DEFAULT NULL COMMENT '房间设施id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间&配套关联表';

DROP TABLE IF EXISTS `room_label`;
CREATE TABLE `room_label` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint DEFAULT NULL COMMENT '房间id',
  `label_id` bigint DEFAULT NULL COMMENT '标签id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间&标签关联表';

DROP TABLE IF EXISTS `room_attr_value`;
CREATE TABLE `room_attr_value` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint DEFAULT NULL COMMENT '房间id',
  `attr_value_id` bigint DEFAULT NULL COMMENT '属性值id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间&基本属性值关联表';

DROP TABLE IF EXISTS `room_lease_term`;
CREATE TABLE `room_lease_term` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint DEFAULT NULL COMMENT '房间id',
  `lease_term_id` bigint DEFAULT NULL COMMENT '租期id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间租期管理表';

DROP TABLE IF EXISTS `room_payment_type`;
CREATE TABLE `room_payment_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint DEFAULT NULL COMMENT '房间id',
  `payment_type_id` bigint DEFAULT NULL COMMENT '支付类型id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间&支付方式关联表';

-- ============================================
-- 8. 业务表
-- ============================================

DROP TABLE IF EXISTS `lease_agreement`;
CREATE TABLE `lease_agreement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '租约id',
  `phone` varchar(11) DEFAULT NULL COMMENT '承租人手机号码',
  `name` varchar(50) DEFAULT NULL COMMENT '承租人姓名',
  `identification_number` varchar(18) DEFAULT NULL COMMENT '承租人身份证号码',
  `apartment_id` bigint DEFAULT NULL COMMENT '签约公寓id',
  `room_id` bigint DEFAULT NULL COMMENT '签约房间id',
  `lease_start_date` date DEFAULT NULL COMMENT '租约开始日期',
  `lease_end_date` date DEFAULT NULL COMMENT '租约结束日期',
  `lease_term_id` bigint DEFAULT NULL COMMENT '租期id',
  `rent` decimal(16,2) DEFAULT NULL COMMENT '租金(元/月)',
  `deposit` decimal(16,2) DEFAULT NULL COMMENT '押金(元)',
  `payment_type_id` bigint DEFAULT NULL COMMENT '支付类型id',
  `status` tinyint DEFAULT NULL COMMENT '租约状态(1:签约待确认 2:已签约 3:已取消 4:已到期 5:退租待确认 6:已退租 7:续约待确认)',
  `source_type` tinyint DEFAULT NULL COMMENT '租约来源(1:新签 2:续约)',
  `additional_info` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租约信息表';

DROP TABLE IF EXISTS `view_appointment`;
CREATE TABLE `view_appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `name` varchar(16) DEFAULT NULL COMMENT '用户姓名',
  `phone` varchar(16) DEFAULT NULL COMMENT '用户手机号码',
  `apartment_id` bigint DEFAULT NULL COMMENT '公寓id',
  `appointment_time` timestamp NULL DEFAULT NULL COMMENT '预约时间',
  `additional_info` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `appointment_status` tinyint DEFAULT NULL COMMENT '预约状态(1:待看房 2:已取消 3:已看房)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房信息表';

DROP TABLE IF EXISTS `browsing_history`;
CREATE TABLE `browsing_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `room_id` bigint DEFAULT NULL COMMENT '浏览房间id',
  `browse_time` timestamp NULL DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史';

-- ============================================
-- 9. 图片
-- ============================================

DROP TABLE IF EXISTS `graph_info`;
CREATE TABLE `graph_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片id',
  `name` varchar(128) DEFAULT NULL COMMENT '图片名称',
  `item_type` tinyint DEFAULT NULL COMMENT '图片所属对象类型(1:公寓 2:房间)',
  `item_id` bigint DEFAULT NULL COMMENT '图片所属对象id',
  `url` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片信息表';

-- ============================================
-- 10. 外键约束 (数据完整性)
-- ============================================

-- 租约 → 公寓 (有活跃租约时禁止删公寓)
ALTER TABLE `lease_agreement` ADD CONSTRAINT `fk_lease_apartment`
    FOREIGN KEY (`apartment_id`) REFERENCES `apartment_info` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- 预约 → 公寓 (有未完成预约时禁止删公寓)
ALTER TABLE `view_appointment` ADD CONSTRAINT `fk_appointment_apartment`
    FOREIGN KEY (`apartment_id`) REFERENCES `apartment_info` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- 房间 → 公寓
ALTER TABLE `room_info` ADD CONSTRAINT `fk_room_apartment`
    FOREIGN KEY (`apartment_id`) REFERENCES `apartment_info` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- ============================================
-- 11. 测试数据
-- ============================================

-- 管理员账号: admin / admin123 (BCrypt)
INSERT INTO `system_user` (`username`, `password`, `name`, `type`, `phone`, `status`)
VALUES ('admin', '$2a$10$0SJgnE9SkMZh27KgcR29bu6fP49qedQK/UP0EscmgTVDQ7LgSAQme', '系统管理员', 1, '13800000000', 1);

-- 普通员工
INSERT INTO `system_user` (`username`, `password`, `name`, `type`, `phone`, `status`)
VALUES ('zhangsan', '$2a$10$brAvIQDg1F1bUzBezEwbHeOfG8WpgLF0NMCb.QJMlir7RwfFPmJpu', '张三', 0, '13800000001', 1);

SET FOREIGN_KEY_CHECKS = 1;
