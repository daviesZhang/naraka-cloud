CREATE TABLE `t_authority`
(
    `ID`              int          NOT NULL AUTO_INCREMENT,
    `RESOURCE`        varchar(255) NOT NULL COMMENT '资源,如get /api/login',
    `RESOURCE_TYPE`   tinyint      NOT NULL COMMENT '资源类型如:URL,MENU',
    `PROCESSOR`       tinyint      DEFAULT NULL COMMENT '处理动作,如过滤,脱敏',
    `PROCESSOR_VALUE` varchar(255) DEFAULT NULL COMMENT '处理值,如字段名',
    `CREATED_BY`      varchar(255) NOT NULL,
    `CREATED_TIME`    datetime     NOT NULL,
    `UPDATED_BY`      varchar(255) DEFAULT NULL,
    `UPDATED_TIME`    datetime     DEFAULT NULL,
    `remark`          varchar(255) DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `t_authority_pk` (`RESOURCE`, `RESOURCE_TYPE`, `PROCESSOR`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 35
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE `t_authority_role`
(
    `ID`           int          NOT NULL AUTO_INCREMENT,
    `AUTHORITY`    int          NOT NULL,
    `CODE`         varchar(255) NOT NULL,
    `REMARK`       varchar(255) DEFAULT NULL,
    `CREATED_BY`   varchar(255) NOT NULL,
    `CREATED_TIME` datetime     NOT NULL,
    `UPDATED_BY`   varchar(255) DEFAULT NULL,
    `UPDATED_TIME` datetime     DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `t_authority_role_pk` (`AUTHORITY`, `CODE`),
    KEY `t_authority_role_CODE_AUTHORITY_index` (`CODE`, `AUTHORITY`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 39
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE `t_category_tree`
(
    `DESCENDANT`    int     NOT NULL COMMENT '代表后代节点',
    `ANCESTOR`      int     NOT NULL COMMENT '代表祖先节点',
    `DISTANCE`      int     NOT NULL COMMENT '祖先距离后代的距离',
    `CATEGORY_TYPE` tinyint NOT NULL COMMENT '类型',
    `CREATED_BY`    varchar(255) DEFAULT NULL,
    `CREATED_TIME`  datetime     DEFAULT NULL,
    PRIMARY KEY (`DESCENDANT`, `ANCESTOR`, `DISTANCE`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE `t_group`
(
    `ID`           int          NOT NULL AUTO_INCREMENT,
    `NAME`         varchar(255) NOT NULL,
    `CODE`         varchar(255) NOT NULL,
    `REMARK`       varchar(255) DEFAULT NULL,
    `CREATED_BY`   varchar(255) NOT NULL,
    `CREATED_TIME` datetime     NOT NULL,
    `UPDATED_BY`   varchar(255) DEFAULT NULL,
    `UPDATED_TIME` datetime     DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE `t_login_log`
(
    `ID`           int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USERNAME`     varchar(32)  NOT NULL COMMENT '用户名',
    `CLIENT_HOST`  varchar(32)  NOT NULL COMMENT '客户端host',
    `LOGIN_TIME`   datetime     NOT NULL COMMENT '登陆时间',
    `SUCCESS`      bit(1)       NOT NULL COMMENT '是否登陆成功',
    `CLIENT_MAC`   varchar(32)  NOT NULL DEFAULT '' COMMENT '客户端mac地址',
    `DEVICE`       varchar(50)  NOT NULL COMMENT '设备',
    `REFERER`      varchar(255) NOT NULL COMMENT '来源',
    `USER_AGENT`   varchar(255) NOT NULL COMMENT '用户代理',
    `CREATED_BY`   varchar(32)  NOT NULL COMMENT '创建人',
    `CREATED_TIME` datetime     NOT NULL COMMENT '创建时间',
    `UPDATED_BY`   varchar(32)  NOT NULL COMMENT '更新人',
    `UPDATED_TIME` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`ID`),
    KEY `i_username` (`USERNAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3 COMMENT ='管理员登陆日子 ';

CREATE TABLE `t_menu`
(
    `ID`           int          NOT NULL AUTO_INCREMENT,
    `URL`          varchar(255) NOT NULL COMMENT 'URL',
    `CODE`         varchar(255) NOT NULL COMMENT 'CODE标识',
    `REMARK`       varchar(255) DEFAULT NULL,
    `CREATED_BY`   varchar(255) NOT NULL,
    `CREATED_TIME` datetime     NOT NULL,
    `UPDATED_BY`   varchar(255) DEFAULT NULL,
    `UPDATED_TIME` datetime     DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `t_operation_log`
(
    `ID`           int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `OPERATOR`     varchar(32)  NOT NULL COMMENT '操作者',
    `CRUD_TYPE`    tinyint      NOT NULL COMMENT '操作类型',
    `CLIENT_IP`    varchar(32)  NOT NULL COMMENT '客户端ip',
    `SUCCESS`      bit(1)       NOT NULL COMMENT '是否成功',
    `DATA_CLASS`   varchar(128) NOT NULL COMMENT '数据类',
    `DIFFERENCE`   varchar(512) DEFAULT NULL COMMENT '差异',
    `CREATED_BY`   varchar(32)  NOT NULL COMMENT '创建人',
    `CREATED_TIME` datetime     NOT NULL COMMENT '创建时间',
    `UPDATED_BY`   varchar(32)  DEFAULT NULL COMMENT '更新人',
    `UPDATED_TIME` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`ID`),
    KEY `i_operator` (`OPERATOR`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 341
  DEFAULT CHARSET = utf8mb3 COMMENT ='操作时间 ';

CREATE TABLE `t_role`
(
    `ID`           int          NOT NULL AUTO_INCREMENT,
    `NAME`         varchar(255) NOT NULL,
    `CODE`         varchar(255) NOT NULL,
    `REMARK`       varchar(255) DEFAULT NULL,
    `CREATED_BY`   varchar(255) NOT NULL,
    `CREATED_TIME` datetime     NOT NULL,
    `UPDATED_BY`   varchar(255) DEFAULT NULL,
    `UPDATED_TIME` datetime     DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `t_user`
(
    `ID`                   int          NOT NULL AUTO_INCREMENT,
    `USERNAME`             varchar(255) NOT NULL,
    `EMAIL`                varchar(255) NOT NULL,
    `PHONE`                varchar(255) NOT NULL,
    `PASSWORD`             varchar(255) NOT NULL,
    `PASSWORD_EXPIRE_TIME` datetime     NOT NULL,
    `TYPE`                 tinyint      NOT NULL DEFAULT '0',
    `STATUS`               tinyint      NOT NULL DEFAULT '0',
    `TWO_VERIFICATION`     varchar(255)          DEFAULT NULL COMMENT '两步验证的secret,不为空则需要验证',
    `REMARK`               varchar(255)          DEFAULT NULL,
    `CREATED_BY`           varchar(255) NOT NULL,
    `CREATED_TIME`         datetime     NOT NULL,
    `UPDATED_BY`           varchar(255)          DEFAULT NULL,
    `UPDATED_TIME`         datetime              DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `t_user_EMAIL_uindex` (`EMAIL`),
    UNIQUE KEY `t_user_PHONE_uindex` (`PHONE`),
    UNIQUE KEY `t_user_USERNAME_uindex` (`USERNAME`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `t_user_group`
(
    `ID`           int          NOT NULL AUTO_INCREMENT,
    `USERNAME`     varchar(255) NOT NULL,
    `CODE`         varchar(255) NOT NULL,
    `REMARK`       varchar(255) DEFAULT NULL,
    `CREATED_BY`   varchar(255) NOT NULL,
    `CREATED_TIME` datetime     NOT NULL,
    `UPDATED_BY`   varchar(255) DEFAULT NULL,
    `UPDATED_TIME` datetime     DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `t_user_role`
(
    `ID`           int          NOT NULL AUTO_INCREMENT,
    `USERNAME`     varchar(255) NOT NULL,
    `CODE`         varchar(255) NOT NULL,
    `REMARK`       varchar(255) DEFAULT NULL,
    `CREATED_BY`   varchar(255) NOT NULL,
    `CREATED_TIME` datetime     NOT NULL,
    `UPDATED_BY`   varchar(255) DEFAULT NULL,
    `UPDATED_TIME` datetime     DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `t_user_role_USERNAME_uindex` (`USERNAME`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

