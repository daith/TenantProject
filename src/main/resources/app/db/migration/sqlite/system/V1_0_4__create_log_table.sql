/*

    1. create table LogData

   */

drop table if exists `log_data`;
CREATE TABLE `log_data` (
                          `id` INTEGER CONSTRAINT `pk_id` PRIMARY KEY AUTOINCREMENT NOT NULL,
                          `method_type` VARCHAR(255) NOT NULL,
                          `url_Data` VARCHAR(255) NOT NULL,
                          `controller` VARCHAR(255) NOT NULL,
                          `header` VARCHAR(255) NOT NULL,
                          `remote_addr_ip` VARCHAR(255) ,
                          `parameter_data` text,
                          `request_body` text ,
                          `response` text,
                          `entry_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                          `response_time` DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                          `waste_time` INTEGER NOT NULL
);