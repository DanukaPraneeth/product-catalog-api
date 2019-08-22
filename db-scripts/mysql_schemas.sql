CREATE TABLE IF NOT EXISTS category (
             `id` int(11) NOT NULL AUTO_INCREMENT,
             `category_type` varchar(32) NOT NULL,
  	      PRIMARY KEY (`id`),
              UNIQUE  KEY (`category_type`)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS product (
             `id` int(11) NOT NULL AUTO_INCREMENT,
	     `product_name` varchar(32) NOT NULL,
             `product_code` int(11)  NOT NULL,
             PRIMARY KEY (`id`),
	     UNIQUE  KEY (`product_code`)
) ENGINE INNODB;

CREATE TABLE IF NOT EXISTS product_category (
             `product_id` int(11) NOT NULL,
	     `category_id` int(11) NOT NULL,
             FOREIGN KEY (`product_id`) references product (`id`),
             FOREIGN KEY (`category_id`) references category (`id`),
	     UNIQUE  KEY (`product_id`, `category_id`)
) ENGINE INNODB;

CREATE TABLE IF NOT EXISTS user (
             `id` int(11) NOT NULL AUTO_INCREMENT,
             `name` varchar(32) NOT NULL,
             `password` varchar(256) NOT NULL,
             `is_admin` boolean NOT NULL,
  	      PRIMARY KEY (`id`),
              UNIQUE  KEY (`name`)
) ENGINE = INNODB;
