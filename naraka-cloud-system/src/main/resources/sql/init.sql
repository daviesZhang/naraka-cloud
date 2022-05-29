drop table if exists sys_authority;
CREATE TABLE `sys_authority`
(
    `id`               varchar(64)  NOT NULL,
    `uri`              varchar(255) NOT NULL,
    `authority_method` tinyint      NOT NULL COMMENT '访问,入参,响应',
    `data`             text         NULL,
    `created_by`       varchar(64)  NOT NULL,
    `created_date`     datetime     NOT NULL,
    `updated_date`     datetime     NULL,
    `updated_by`       varchar(64)  NULL,
    PRIMARY KEY (`id`)
);


drop table if exists sys_role;
CREATE TABLE `sys_role`
(
    `code`         varchar(64) NOT NULL,
    `name`         varchar(64) NOT NULL,
    `created_by`   varchar(64) NOT NULL,
    `created_date` datetime    NOT NULL,
    `updated_date` datetime    NULL,
    `updated_by`   varchar(64) NULL,
    PRIMARY KEY (`code`)
);


drop table if exists sys_role_authority;
CREATE TABLE `sys_role_authority`
(
    `role_code`    varchar(64) NOT NULL,
    `authority_id` varchar(64) NOT NULL,
    `created_by`   varchar(64) NOT NULL,
    `created_date` datetime    NOT NULL,
    PRIMARY KEY (`role_code`, `authority_id`)
);
drop table if exists sys_tenement;
CREATE TABLE `sys_tenement`
(
    `id`           varchar(64)  NOT NULL,
    `name`         varchar(64)  NOT NULL,
    `desc`         varchar(255) NULL,
    `logic_delete` tinyint(1)   NOT NULL DEFAULT 0,
    `created_by`   varchar(64)  NOT NULL,
    `created_date` datetime     NOT NULL,
    `updated_date` datetime     NULL,
    `updated_by`   varchar(64)  NULL,
    PRIMARY KEY (`id`)
);


drop table if exists sys_tenement_role;
CREATE TABLE `sys_tenement_role`
(
    `tenement_id`  varchar(64) NOT NULL,
    `role_code`    varchar(64) NOT NULL,
    `created_by`   varchar(64) NOT NULL,
    `created_date` datetime    NOT NULL,
    PRIMARY KEY (`tenement_id`, `role_code`)
);
drop table if exists sys_user;
CREATE TABLE `sys_user`
(
    `id`           varchar(64)  NOT NULL,
    `username`     varchar(64)  NOT NULL,
    `password`     varchar(128) NOT NULL,
    `email`        varchar(128) NULL,
    `phone`        varchar(128) NULL,
    `logic_delete` tinyint(1)   NOT NULL DEFAULT 0,
    `created_by`   varchar(64)  NOT NULL,
    `created_date` datetime     NOT NULL,
    `updated_date` datetime     NULL,
    `updated_by`   varchar(64)  NULL,
    PRIMARY KEY (`id`),
    INDEX `user_name_index` (`username`),
    INDEX `user_phone_index` (`phone`)
);
drop table if exists sys_user_role;
CREATE TABLE `sys_user_role`
(
    `user_id`      varchar(64) NOT NULL,
    `role_code`    varchar(64) NOT NULL,
    `created_by`   varchar(64) NOT NULL,
    `created_date` datetime    NOT NULL,
    PRIMARY KEY (user_id, role_code)
);
drop table if exists sys_user_tenement;
CREATE TABLE `sys_user_tenement`
(
    `user_id`      varchar(64) NOT NULL,
    `tenement_id`  varchar(64) NOT NULL,
    `created_by`   varchar(64) NOT NULL,
    `created_date` datetime    NOT NULL,
    PRIMARY KEY (user_id, tenement_id)
);


