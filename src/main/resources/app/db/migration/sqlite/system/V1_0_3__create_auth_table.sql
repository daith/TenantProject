/*

    1. create table Tenant
    2. create table Member
    3. insert Tenant data

   */

drop table if exists `account`;
CREATE TABLE `account` (
                          `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                          `name` VARCHAR(255) NOT NULL,
                          `password` VARCHAR(255) NOT NULL,
                          `token` VARCHAR(255) ,
                          `tenant_id` INTEGER NOT NULL,
                          `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                          `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                          `is_deleted` BOOLEAN NOT NULL DEFAULT false
);



drop table if exists `account_role`;
CREATE TABLE `account_role` (
                           `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                           `account_id` INTEGER NOT NULL,
                           `role_id` INTEGER NOT NULL,
                           `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                           `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                           `is_deleted` BOOLEAN NOT NULL DEFAULT false
);

drop table if exists `function`;
CREATE TABLE `function` (
                                `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                                `group_type` VARCHAR(255) NOT NULL,
                                `function_type` VARCHAR(255) NOT NULL,
                                `function_name` VARCHAR(255) NOT NULL,
                                `description` VARCHAR(255) NOT NULL,
                                `display_order` INTEGER NOT NULL,
                                `tenant_id` INTEGER NOT NULL,
                                `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                                `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                                `is_deleted` BOOLEAN NOT NULL DEFAULT false,
                                `data_source_id` INTEGER NOT NULL
);

drop table if exists `role`;
CREATE TABLE `role` (
                            `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `role_name` VARCHAR(255) NOT NULL,
                            `tenant_id` INTEGER NOT NULL,
                            `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                            `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                            `is_deleted` BOOLEAN NOT NULL DEFAULT false
);


drop table if exists `role_function`;
CREATE TABLE `role_function` (
                        `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `function_id` INTEGER NOT NULL,
                        `role_id` INTEGER NOT NULL,
                        `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                        `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                        `is_deleted` BOOLEAN NOT NULL DEFAULT false
);