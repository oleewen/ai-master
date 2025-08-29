-- 申诉单表
CREATE TABLE IF NOT EXISTS `appeal` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉单ID',
    `appealer_id` bigint NOT NULL COMMENT '申诉方ID',
    `reason` varchar(500) DEFAULT NULL COMMENT '申诉理由',
    `status` varchar(20) NOT NULL DEFAULT 'CREATED' COMMENT '申诉状态',
    `attachments` text COMMENT '附件列表，JSON格式',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_appealer_id` (`appealer_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉单表';

-- 申诉项表
CREATE TABLE IF NOT EXISTS `appeal_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉项ID',
    `appeal_id` bigint NOT NULL COMMENT '申诉单ID',
    `type` varchar(20) NOT NULL COMMENT '申诉类型',
    `title` varchar(100) DEFAULT NULL COMMENT '申诉项标题',
    `status` varchar(20) NOT NULL COMMENT '申诉项状态',
    `content` text COMMENT '申诉内容',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_appeal_id` (`appeal_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉项表';

-- 申诉方表
CREATE TABLE IF NOT EXISTS `appealer` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉方ID',
    `code` varchar(50) NOT NULL COMMENT '申诉方编码',
    `type` varchar(20) NOT NULL COMMENT '申诉方类型',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉方表';

-- 插入测试数据
INSERT INTO `appealer` (`code`, `type`) VALUES
('SITE_001', 'SITE'),
('SITE_002', 'SITE'),
('CENTER_001', 'CENTER'),
('HEADQUARTERS_001', 'HEADQUARTERS');