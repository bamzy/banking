/*Table structure for table `connectiontype` */

DROP TABLE IF EXISTS `connectiontype`;

CREATE TABLE `connectiontype` (
  `id`   BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` CHAR(50)   NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8;

/*Table structure for table `engine` */

DROP TABLE IF EXISTS `engine`;

CREATE TABLE `engine` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`        CHAR(255)  NOT NULL,
  `description` VARCHAR(1000)       DEFAULT NULL,
  `version`     BIGINT(20) NOT NULL,
  `data`        MEDIUMBLOB NOT NULL,
  `checksum`    TINYBLOB,
  `dataLength`  INT(32)    NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =19
  DEFAULT CHARSET =utf8;

/*Table structure for table `terminaltype` */

DROP TABLE IF EXISTS `terminaltype`;
CREATE TABLE `terminaltype` (
  `id`   BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` CHAR(50)   NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =6
  DEFAULT CHARSET =utf8;

/*Table structure for table `terminalgroup` */

DROP TABLE IF EXISTS `terminalgroup`;

CREATE TABLE `terminalgroup` (
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`         CHAR(100)  NOT NULL,
  `active`       TINYINT(1) NOT NULL,
  `dailyMessage` VARCHAR(1000)       DEFAULT NULL,
  `groupId`      BIGINT(20)          DEFAULT NULL,
  `engineId`     BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `groupId` (`groupId`),
  KEY `engineId` (`engineId`),
  CONSTRAINT `terminalgroup_ibfk_2` FOREIGN KEY (`groupId`) REFERENCES `terminalgroup` (`id`),
  CONSTRAINT `terminalgroup_ibfk_3` FOREIGN KEY (`engineId`) REFERENCES `engine` (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =6
  DEFAULT CHARSET =utf8;

/*Table structure for table `terminal` */

DROP TABLE IF EXISTS `terminal`;

CREATE TABLE `terminal` (
  `terminalNumber`    BIGINT(20) NOT NULL,
  `status`            CHAR(50)   NOT NULL,
  `active`            TINYINT(1) NOT NULL,
  `startupDate`       BIGINT(20) NOT NULL,
  `description`       VARCHAR(1000) DEFAULT NULL,
  `maxPasswordLength` INT(20)    NOT NULL,
  `lastVisit`         BIGINT(20)    DEFAULT NULL,
  `nextVisit`         BIGINT(20)    DEFAULT NULL,
  `merchantId`        BIGINT(20) NOT NULL,
  `groupId`           BIGINT(20)    DEFAULT NULL,
  `terminalTypeId`    BIGINT(20)    DEFAULT NULL,
  PRIMARY KEY (`terminalNumber`),
  KEY `groupId` (`groupId`),
  KEY `lastVisit` (`lastVisit`),
  KEY `terminalTypeId` (`terminalTypeId`),
  CONSTRAINT `terminal_ibfk_1` FOREIGN KEY (`groupId`) REFERENCES `terminalgroup` (`id`),
/*CONSTRAINT `terminal_ibfk_2` FOREIGN KEY (`lastVisit`) REFERENCES `access` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,*/
  CONSTRAINT `terminal_ibfk_3` FOREIGN KEY (`terminalTypeId`) REFERENCES `terminaltype` (`id`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8;

/*Table structure for table `request` */

DROP TABLE IF EXISTS `request`;

CREATE TABLE `request` (
  `terminalNumber` BIGINT(20) NOT NULL,
  `date`           BIGINT(20) NOT NULL,
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type`           CHAR(100)  NOT NULL,
  `description`    VARCHAR(1000)       DEFAULT NULL,
  `status`         CHAR(100)  NOT NULL,
  `operatorPath`   CHAR(255)           DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `terminalNumber` (`terminalNumber`),
  KEY `operatorId` (`operatorPath`),
  CONSTRAINT `request_ibfk_1` FOREIGN KEY (`terminalNumber`) REFERENCES `terminal` (`terminalNumber`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =96
  DEFAULT CHARSET =utf8;


DROP TABLE IF EXISTS `access`;
CREATE TABLE `access` (
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `terminalNumber` BIGINT(20)          DEFAULT NULL,
  `timeStamp`      BIGINT(20) NOT NULL,
  `accessType`     CHAR(50)   NOT NULL,
  `nightCode`      CHAR(255)           DEFAULT NULL,
  `status`         CHAR(255)           DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `terminalNumber` (`terminalNumber`),
  CONSTRAINT `access_ibfk_1` FOREIGN KEY (`terminalNumber`) REFERENCES `terminal` (`terminalNumber`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =1137
  DEFAULT CHARSET =utf8;


/*Table structure for table `assignhistory` */

DROP TABLE IF EXISTS `assignhistory`;

CREATE TABLE `assignhistory` (
  `security_activeUserPath` CHAR(255)
                            CHARACTER SET latin1 NOT NULL,
  `requestId`               BIGINT(20)           NOT NULL,
  `operatorPath`            CHAR(255)
                            CHARACTER SET latin1 NOT NULL,
  `description`             VARCHAR(1000)
                            CHARACTER SET latin1 DEFAULT NULL,
  `id`                      BIGINT(20)           NOT NULL,
  `timeStamp`               BIGINT(20)           NOT NULL,
  PRIMARY KEY (`id`),
  KEY `requestId` (`requestId`),
  CONSTRAINT `assignhistory_ibfk_1` FOREIGN KEY (`requestId`) REFERENCES `request` (`id`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8mb4;

/*Table structure for table `changes` */

DROP TABLE IF EXISTS `changes`;

CREATE TABLE `changes` (
  `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
  `timeStamp`               BINARY(20) NOT NULL,
  `tableName`               CHAR(150)  NOT NULL,
  `description`             TEXT       DEFAULT NULL,
  `recordId`                BIGINT(20) NOT NULL,
  `security_activeUserPath` CHAR(255)  NOT NULL,
  `actionType`              CHAR(20)   NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =2681
  DEFAULT CHARSET =utf8;

/*Table structure for table `charvalue` */

DROP TABLE IF EXISTS `charvalue`;

CREATE TABLE `charvalue` (
  `id`       BIGINT(20)    NOT NULL AUTO_INCREMENT,
  `name`     CHAR(100)     NOT NULL,
  `value`    VARCHAR(1000) NOT NULL,
  `accessId` BIGINT(20)    NOT NULL,
  PRIMARY KEY (`id`),
  KEY `accessId` (`accessId`),
  CONSTRAINT `charvalue_ibfk_1` FOREIGN KEY (`accessId`) REFERENCES `access` (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =885
  DEFAULT CHARSET =utf8;

/*Table structure for table `configparameter` */

DROP TABLE IF EXISTS `configparameter`;

CREATE TABLE `configparameter` (
  `version`      BIGINT(20)         NOT NULL,
  `id`           BIGINT(20)         NOT NULL,
  `availability` CHAR(50)
                 CHARACTER SET utf8 DEFAULT NULL,
  `configName`   CHAR(50)
                 CHARACTER SET utf8 NOT NULL,
  `configValue`  CHAR(255)
                 CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`),
  KEY `version` (`version`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8mb4;


/*Table structure for table `connectiontype_terminal` */

DROP TABLE IF EXISTS `connectiontype_terminal`;

CREATE TABLE `connectiontype_terminal` (
  `connectionTypeId` BIGINT(20) NOT NULL,
  `terminalNumber`   BIGINT(20) NOT NULL,
  PRIMARY KEY (`connectionTypeId`, `terminalNumber`),
  KEY `terminalNumber` (`terminalNumber`),
  CONSTRAINT `connectiontype_terminal_ibfk_1` FOREIGN KEY (`connectionTypeId`) REFERENCES `connectiontype` (`id`),
  CONSTRAINT `connectiontype_terminal_ibfk_2` FOREIGN KEY (`terminalNumber`) REFERENCES `terminal` (`terminalNumber`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8;


/*Table structure for table `engine_plugintemplate` */

DROP TABLE IF EXISTS `engine_plugintemplate`;

CREATE TABLE `engine_plugintemplate` (
  `engineId`         BIGINT(20) NOT NULL,
  `pluginTemplateId` BIGINT(20) NOT NULL,
  `id`               BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `engineId` (`engineId`),
  KEY `pluginTemplateId` (`pluginTemplateId`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =latin1;

/*Table structure for table `integervalue` */

DROP TABLE IF EXISTS `integervalue`;

CREATE TABLE `integervalue` (
  `id`       BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`     CHAR(100)  NOT NULL,
  `value`    INT(255)   NOT NULL,
  `accessId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `accessId` (`accessId`),
  CONSTRAINT `integervalue_ibfk_1` FOREIGN KEY (`accessId`) REFERENCES `access` (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =821
  DEFAULT CHARSET =utf8;
/*Table structure for table `plugintemplate` */

DROP TABLE IF EXISTS `plugintemplate`;

CREATE TABLE `plugintemplate` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `title`       CHAR(255)  NOT NULL,
  `description` TEXT,
  `version`     BIGINT(20) NOT NULL,
  `content`     TEXT       NOT NULL,
  `name`        CHAR(50)   NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =13
  DEFAULT CHARSET =utf8;

/*Table structure for table `plugin` */

DROP TABLE IF EXISTS `plugin`;

CREATE TABLE `plugin` (
  `id`               BIGINT(20) NOT NULL,
  `pluginTemplateId` BIGINT(20) NOT NULL,
  `pluginValues`     TEXT       ,
  `name`             CHAR(50)   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pluginTemplateId` (`pluginTemplateId`),
  CONSTRAINT `plugin_ibfk_1` FOREIGN KEY (`pluginTemplateId`) REFERENCES `plugintemplate` (`id`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8;

/*Table structure for table `pluginstatus` */

DROP TABLE IF EXISTS `pluginstatus`;

CREATE TABLE `pluginstatus` (
  `id`                    BIGINT(20) NOT NULL,
  `accessId`              BIGINT(20) NOT NULL,
  `pluginTemplateTitle`   CHAR(255)  NOT NULL,
  `pluginTemplateVersion` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `accessId` (`accessId`),
  CONSTRAINT `pluginstatus_ibfk_1` FOREIGN KEY (`accessId`) REFERENCES `access` (`id`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8;


/*Table structure for table `pos` */

DROP TABLE IF EXISTS `pos`;

CREATE TABLE `pos` (
  `id`             BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `serialNumber`   BIGINT(20)  NOT NULL,
  `assetNumber`    VARCHAR(30) NOT NULL,
  `terminalNumber` BIGINT(20)  NOT NULL,
  PRIMARY KEY (`id`, `serialNumber`),
  KEY `terminalNumber` (`terminalNumber`),
  CONSTRAINT `pos_ibfk_2` FOREIGN KEY (`terminalNumber`) REFERENCES `terminal` (`terminalNumber`)
)
  ENGINE =INNODB
  AUTO_INCREMENT =60541810
  DEFAULT CHARSET =utf8;


/*Table structure for table `terminalgroup_plugin` */

DROP TABLE IF EXISTS `terminalgroup_plugin`;

CREATE TABLE `terminalgroup_plugin` (
  `id`              BIGINT(20) NOT NULL,
  `terminalGroupId` BIGINT(20) NOT NULL,
  `pluginId`        BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pluginId` (`pluginId`),
  KEY `terminalGroupId` (`terminalGroupId`),
  CONSTRAINT `terminalgroup_plugin_ibfk_2` FOREIGN KEY (`pluginId`) REFERENCES `plugin` (`id`),
  CONSTRAINT `terminalgroup_plugin_ibfk_3` FOREIGN KEY (`terminalGroupId`) REFERENCES `terminalgroup` (`id`)
)
  ENGINE =INNODB
  DEFAULT CHARSET =utf8;

ALTER TABLE engine_plugintemplate
ADD CONSTRAINT `engine_plugintemplate_ibfk_1` FOREIGN KEY (`engineId`) REFERENCES `engine` (`id`) , ADD CONSTRAINT `engine_plugintemplate_ibfk_2` FOREIGN KEY (`pluginTemplateId`) REFERENCES `plugintemplate` (`id`);


