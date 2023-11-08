/*

    1. create table Tenant
    2. create table Member
    3. insert Tenant data

   */

drop table if exists `tenant`;
CREATE TABLE `tenant` (
  `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
  `company_name` VARCHAR(255) CONSTRAINT `uk_company_name` UNIQUE NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
  `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
  `owner_id` INTEGER DEFAULT NULL,
  `create_by_id` INTEGER DEFAULT NULL,
  `update_by_id` INTEGER DEFAULT NULL,
  `status` VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
  `is_deleted` BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO `tenant` (`company_name`)
VALUES ('datarget-company');

INSERT INTO `tenant` (`company_name`)
VALUES ('demo-company');

drop table if exists `member`;
CREATE TABLE `member` (
   `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
   `name` VARCHAR(255) NOT NULL,
   `mail` VARCHAR(255) CONSTRAINT `uk_mail` UNIQUE NOT NULL,
   `password` VARCHAR(255) NOT NULL,
   `token` VARCHAR(255) CONSTRAINT `uk_token` UNIQUE NOT NULL,
   `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
   `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
   `create_by_id` INTEGER DEFAULT NULL,
   `update_by_id` INTEGER DEFAULT NULL,
   `status` VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
   `is_deleted` BOOLEAN NOT NULL DEFAULT false
);

