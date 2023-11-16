/*

    1. create table ColumnConfig
    2. create table DataSourceConfig
    3. create table TableConfig

   */

drop table if exists `data_source_config`;

CREATE TABLE `data_source_config` (
  `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
  `name` VARCHAR(255) CONSTRAINT `uk_name` UNIQUE NOT NULL,
  `caption` VARCHAR(255) DEFAULT '',
  `description` VARCHAR(255)  DEFAULT '',
  `display_order` INTEGER DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
  `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
  `create_by_id` INTEGER DEFAULT NULL,
  `update_by_id` INTEGER DEFAULT NULL,
  `status` VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
  `is_deleted` BOOLEAN NOT NULL DEFAULT false,
  `database_type` VARCHAR(255)   NOT NULL,
  `driver_class_name` VARCHAR(255) NOT NULL,
  `url` VARCHAR(255)  NOT NULL,
  `username` VARCHAR(255)  NOT NULL,
  `password` VARCHAR(255)  NOT NULL,
  `metadata_table_prefix` VARCHAR(255)  DEFAULT '',
  `metadata_database_naming` VARCHAR(255)  DEFAULT '',
  `business_table_prefix` VARCHAR(255)  DEFAULT '',
  `business_database_naming` VARCHAR(255)  DEFAULT '',
  `tenant_id` INTEGER NOT NULL
);


drop table if exists `column_config`;
CREATE TABLE `column_config` (
`id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
`name` VARCHAR(255) ,
`caption` VARCHAR(255) DEFAULT '',
`create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
`update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
`create_by_id` INTEGER DEFAULT NULL,
`update_by_id` INTEGER DEFAULT NULL,
`status` VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
`is_deleted` BOOLEAN NOT NULL DEFAULT false,
`is_default` BOOLEAN NOT NULL DEFAULT false,
`api_resource_naming` VARCHAR(255)  ,
`api_param_naming` VARCHAR(255) ,
`object_naming` VARCHAR(255)  ,
`tenant_id` INTEGER NOT NULL,
`table_id` INTEGER NOT NULL,
`length` VARCHAR(255)  DEFAULT '',
`PK_TYPE` VARCHAR(255)  DEFAULT '',
`fk_table_name` VARCHAR(255)  DEFAULT '',
`fk_column` VARCHAR(255)  DEFAULT ''
);

drop table if exists `table_config`;
-- table_config definiti
CREATE TABLE `table_config` (
                                `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                                `name` VARCHAR(255) ,
                                `category` VARCHAR(255) DEFAULT '',
                                `caption` VARCHAR(255) DEFAULT '',
                                `module_name` VARCHAR(255) DEFAULT '',
                                `display_order` INTEGER DEFAULT 0,
                                `create_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                                `update_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                                `create_by_id` INTEGER DEFAULT NULL,
                                `update_by_id` INTEGER DEFAULT NULL,
                                `status` VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
                                `is_deleted` BOOLEAN NOT NULL DEFAULT false,
                                `tenant_id` INTEGER NOT NULL,
                                `data_source_id` INTEGER NOT NULL);


-- selection_value definition
drop table if exists `selection_value`;

CREATE TABLE selection_value (
                                 `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                                 create_time timestamp,
                                 is_deleted boolean,
                                 update_time timestamp,
                                 client_column_key varchar(255),
                                 column_name varchar(255),
                                 parent_column_key varchar(255),
                                 parent_table_name varchar(255),
                                 parent_table_id INTEGER ,
                                 system_column_key varchar(255),
                                 system_column_value varchar(255),
                                 table_id INTEGER NOT NULL,
                                 table_name varchar(255),
                                 tenant_id bigint  NOT NULL,
                                 `data_source_id` INTEGER NOT NULL );