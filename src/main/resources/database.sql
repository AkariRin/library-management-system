create table
    user(
            uuid varchar(36) not null primary key,
            username varchar(50) not null unique,
            name varchar(12) not null,
            password varchar(60) not null,
            admin boolean not null default false
);

-- 图书信息表（存储图书品种信息）
CREATE TABLE `books` (
                         `book_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图书ID（主键）',
                         `isbn` varchar(20) DEFAULT NULL COMMENT '国际标准书号',
                         `title` varchar(50) NOT NULL COMMENT '书名',
                         `author` varchar(50) NOT NULL COMMENT '作者',
                         `publisher` varchar(50) DEFAULT NULL COMMENT '出版社',
                         `publish_date` date DEFAULT NULL COMMENT '出版日期',
                         `category` int(11) DEFAULT NULL COMMENT '分类',
                         `summary` text COMMENT '内容简介',
                         `price` decimal(10,2) DEFAULT NULL COMMENT '定价',
                         PRIMARY KEY (`book_id`),
                         UNIQUE KEY `uk_isbn` (`isbn`),
                         KEY `idx_title` (`title`),
                         KEY `idx_author` (`author`),
                         KEY `idx_publish_date` (`publish_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表（品种信息）';

-- 图书副本表（存储具体物理书籍信息）
CREATE TABLE `book_items` (
                              `item_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '副本ID（主键）',
                              `book_id` int(11) NOT NULL COMMENT '图书ID（外键，关联books表）',
                              `barcode` varchar(50) NOT NULL COMMENT '条码号（物理书籍唯一标识）',
                              `location` varchar(50) DEFAULT NULL COMMENT '馆藏位置',
                              `status` enum('Available','Checked Out','Lost','Damaged','Withdrawn') DEFAULT 'Available' COMMENT '副本状态',
                              `acquisition_date` date DEFAULT NULL COMMENT '入库日期',
                              `acquisition_price` decimal(10,2) DEFAULT NULL COMMENT '采购价格',
                              `notes` varchar(500) DEFAULT NULL COMMENT '备注信息',
                              PRIMARY KEY (`item_id`),
                              UNIQUE KEY `uk_barcode` (`barcode`),
                              KEY `idx_book_id` (`book_id`),
                              KEY `idx_status` (`status`),
                              KEY `idx_location` (`location`),
                              CONSTRAINT `fk_book_items_book_id` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书副本表（具体物理书籍）';