DROP DATABASE IF EXISTS dmp;
CREATE DATABASE IF NOT EXISTS dmp;
USE dmp;

SET FOREIGN_KEY_CHECKS = OFF;
########################################################################################################################


DROP TABLE IF EXISTS user_account;
CREATE TABLE IF NOT EXISTS user_account
(
  id       INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '自增ID',
  username VARCHAR(64)                        NOT NULL UNIQUE,
  password VARCHAR(128)                       NOT NULL,
  salt     VARCHAR(64)                        NOT NULL,
  realname VARCHAR(32),
  usercode VARCHAR(32) COMMENT '人员编号',
  email    VARCHAR(64),
  mobile   VARCHAR(13),
  status   SMALLINT(1) DEFAULT 0 COMMENT '账户状态：0-正常，1-锁定，2-注销',
  guid     VARCHAR(64) COMMENT '导入的用户ID'
) COMMENT '用户账户表';

DROP TABLE IF EXISTS user_profile;
CREATE TABLE IF NOT EXISTS user_profile
(
  id      INTEGER PRIMARY KEY COMMENT '用户ID',
  gender  TINYINT(1) COMMENT '用户性别',
  avatar  VARCHAR(64) COMMENT '用户头像',
  address VARCHAR(128) COMMENT '用户住址',
  CONSTRAINT user_id_fk FOREIGN KEY (id) REFERENCES user_account (id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '用户详情表';

DROP TABLE IF EXISTS user_login_record;
CREATE TABLE IF NOT EXISTS user_login_record
(
  login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户登录时间',
  user_id    INTEGER   NOT NULL,
  login_ip   VARCHAR(32) COMMENT '登录IP',
  status     TINYINT(1) DEFAULT 1 COMMENT '用户登录状态，成功/失败',
  PRIMARY KEY (user_id, login_time)
) ENGINE = InnoDB COMMENT '用户登录记录';

DROP TABLE IF EXISTS role;
CREATE TABLE IF NOT EXISTS role
(
  id            INTEGER AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY COMMENT '自增角色ID',
  name          VARCHAR(32)            NOT NULL,
  department_id INTEGER COMMENT '角色所属部门',
  description   VARCHAR(256) COMMENT '角色说明',
  guid          VARCHAR(64) COMMENT '导入的角色ID'
) COMMENT '用户角色表';

DROP TABLE IF EXISTS department;
CREATE TABLE IF NOT EXISTS department
(
  id           INTEGER AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY COMMENT '部门ID',
  name         VARCHAR(32)            NOT NULL,
  parent_id    INTEGER COMMENT '上级部门ID',
  type         TINYINT COMMENT '部门类别：公司/部门/组',
  chief_leader INTEGER COMMENT '部门正领导',
  vice_leader  INTEGER COMMENT '部门副领导',
  description  VARCHAR(256) COMMENT '部门说明',
  guid         VARCHAR(64) COMMENT '导入的部门ID',
  FOREIGN KEY (chief_leader) REFERENCES user_account (id),
  FOREIGN KEY (vice_leader) REFERENCES user_account (id)
) COMMENT '部门表';

DROP TABLE IF EXISTS resource;
CREATE TABLE IF NOT EXISTS resource
(
  id          INTEGER AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(64) UNIQUE,
  type        VARCHAR(8) COMMENT '资源类型：MENU/BUTTON/API',
  code        VARCHAR(32) COMMENT '资源编码',
  parent_id   INTEGER COMMENT '上级资源ID',
  `rank`      INTEGER COMMENT '资源排序字段',
  uri         VARCHAR(128),
  method      VARCHAR(8) COMMENT '请求方法：GET/POST/PUT/PATCH/HEAD/DELETE',
  status      TINYINT(1) COMMENT '资源状态：NORMAL/BLOCKED',
  description VARCHAR(128)
) COMMENT '资源表';

DROP TABLE IF EXISTS r_user_role;
CREATE TABLE IF NOT EXISTS r_user_role
(
  user_id INTEGER,
  role_id INTEGER,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES user_account (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '用户角色表';

DROP TABLE IF EXISTS r_user_department;
CREATE TABLE IF NOT EXISTS r_user_department
(
  user_id       INTEGER,
  department_id INTEGER,
  PRIMARY KEY (user_id, department_id),
  FOREIGN KEY (user_id) REFERENCES user_account (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '用户部门关联表';

DROP TABLE IF EXISTS r_role_department;
CREATE TABLE IF NOT EXISTS r_role_department
(
  role_id       INTEGER,
  department_id INTEGER,
  PRIMARY KEY (role_id, department_id),
  FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '角色部门关联表';

DROP TABLE IF EXISTS r_role_resource;
CREATE TABLE IF NOT EXISTS r_role_resource
(
  role_id     INTEGER,
  resource_id INTEGER,
  PRIMARY KEY (role_id, resource_id),
  FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (resource_id) REFERENCES resource (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '角色资源关联表';

########################################################################################################################

DROP TABLE IF EXISTS schedule_job;
CREATE TABLE schedule_job
(
  id             INTEGER AUTO_INCREMENT PRIMARY KEY,
  name           VARCHAR(32)                          null,
  `group`        VARCHAR(32)                          null,
  status         VARCHAR(10)                          null COMMENT '任务状态，同Trigger.status',
  type           VARCHAR(16)                          null COMMENT '调度任务类型',
  cron           VARCHAR(64)                          null,
  clazz          VARCHAR(64)                          null,
  bean           VARCHAR(64)                          null,
  method         VARCHAR(64)                          null,
  argument       BLOB                                 null,
  effect_time    DATETIME                            null COMMENT '任务生效时间',
  expire_time    DATETIME                            null COMMENT '任务失效时间',
  misfire_policy tinyint(1) DEFAULT 0 COMMENT 'Misfire 策略,0不处理，1立即执行，2？',
  concurrent     tinyint(1) DEFAULT 0                 null COMMENT '是否允许并发',
  description    VARCHAR(1024)                        null COMMENT '调度任务描述',
  created_at     DATETIME  COMMENT '创建时间',
  created_by     INTEGER                              null,
  updated_at     TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  updated_by     INTEGER COMMENT '更新人',
  deleted        TINYINT COMMENT '是否已删除',
  deleted_by     INTEGER COMMENT '删除人',
  deleted_at     DATETIME  COMMENT '删除时间',
  CONSTRAINT UNIQUE (`group`, name)
) COMMENT '调试任务表';

DROP TABLE IF EXISTS schedule_history;
CREATE TABLE IF NOT EXISTS schedule_history
(
  id             INTEGER AUTO_INCREMENT PRIMARY KEY,
  job_id         INTEGER   NOT NULL,
  job_name       VARCHAR(32),
  start_time     DATETIME NOT NULL COMMENT '任务开始时间',
  finish_time    DATETIME COMMENT '任务结束时间',
  next_fire_time DATETIME COMMENT '下次触发时间',
  success        TINYINT(1) DEFAULT 0 COMMENT '任务执行结果，成功/失败',
  message        TEXT       DEFAULT null COMMENT '任务返回信息'
) ENGINE = ARCHIVE COMMENT '任务触发执行纪录表';

DROP TABLE IF EXISTS `option`;
CREATE TABLE IF NOT EXISTS `option`
(
  id          INTEGER AUTO_INCREMENT PRIMARY KEY,
  namespace   VARCHAR(32) UNIQUE NOT NULL DEFAULT 'global' COMMENT '字段项命名空间',
  `group`     VARCHAR(32) COMMENT '字典项分组',
  name        VARCHAR(32) COMMENT '字典项名称',
  value       VARCHAR(32) COMMENT '字典项值',
  `rank`      SMALLINT COMMENT '排序字段',
  parent_id   INTEGER                     DEFAULT 0 COMMENT '父节点ID',
  description VARCHAR(256) COMMENT '字典项说明'
) COMMENT '字典项表';

########################################################################################################################

DROP TABLE IF EXISTS md_catalog;
CREATE TABLE IF NOT EXISTS md_catalog
(
  id        VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
  name      VARCHAR(32) NOT NULL UNIQUE COMMENT '系统类别名称',
  parent_id INTEGER     NOT NULL DEFAULT 0 COMMENT '上级类别',
  context   VARCHAR(128) COMMENT '路径',
  `rank`    TINYINT              DEFAULT 0 COMMENT '类别次序',
  remark    VARCHAR(256) COMMENT '系统类别说明'
) COMMENT '系统类别表';

DROP TABLE IF EXISTS md_system;
CREATE TABLE IF NOT EXISTS md_system
(
  id         VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
  name       VARCHAR(32) NOT NULL UNIQUE COMMENT '系统名称',
  catalog_id VARCHAR(64) COMMENT '所属类别',
  remark     VARCHAR(256) COMMENT '系统说明',
  FOREIGN KEY (catalog_id) REFERENCES md_catalog (id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '系统表';

DROP TABLE IF EXISTS md_datasource;
CREATE TABLE IF NOT EXISTS md_datasource
(
  id          VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(32) NOT NULL UNIQUE,
  label       VARCHAR(32) COMMENT '数据源显示名称',
  type        VARCHAR(8) COMMENT '数据源类型',
  source      VARCHAR(32) COMMENT '数据源来源',
  status      TINYINT(1) COMMENT '数据源状态',
  host        VARCHAR(128) COMMENT '数据源连接URL',
  port        INTEGER(6) COMMENT '数据源连接端口',
  user        VARCHAR(16) COMMENT '数据源连接用户名',
  pass        VARCHAR(32) COMMENT '数据源连接密码',
  version     VARCHAR(128) COMMENT '数据源版本',
  description VARCHAR(256) COMMENT '数据源说明',
  system_id   VARCHAR(64) COMMENT '数据源所属系统',
  created_by  INTEGER COMMENT '数据源添加人',
  created_at  DATETIME  COMMENT '数据源添加时间',
  updated_by  INTEGER COMMENT '数据源信息修改人',
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '数据源信息修改时间',
  FOREIGN KEY (system_id) REFERENCES md_system (id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '数据源表';

DROP TABLE IF EXISTS md_database;
CREATE TABLE IF NOT EXISTS md_database
(
  id            VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
  name          VARCHAR(32) NOT NULL COMMENT '数据库名称 catalog',
  label         VARCHAR(32) COMMENT '数据库显示名称',
  remark        VARCHAR(256) COMMENT '数据库备注信息',
  datasource_id VARCHAR(64),
  FOREIGN KEY (datasource_id) REFERENCES md_datasource (id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '数据库表';

DROP TABLE IF EXISTS md_table;
CREATE TABLE IF NOT EXISTS md_table
(
  id            VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
  name          VARCHAR(32) NOT NULL COMMENT '表名',
  label         VARCHAR(32) COMMENT '表显示名称',
  `schema`      VARCHAR(32) COMMENT '表所属schema',
  `rank`        INTEGER DEFAULT 0 COMMENT '表排序',
  primary_key   VARCHAR(32) COMMENT '表主键字段名称，若为联合主键则为主键逗号分隔值',
  ddl           VARCHAR(1024) COMMENT '建表语句',
  remark        VARCHAR(256) COMMENT '表备注信息',
  database_id   VARCHAR(64) COMMENT '所属数据库',
  datasource_id VARCHAR(64) COMMENT '所属数据源',
  FOREIGN KEY (database_id) REFERENCES md_database (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (datasource_id) REFERENCES md_datasource (id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '数据表表';

DROP TABLE IF EXISTS md_column;
CREATE TABLE IF NOT EXISTS md_column
(
  id              VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
  name            VARCHAR(32) NOT NULL COMMENT '字段名',
  label           VARCHAR(32) COMMENT '字段显示名称',
  alias           VARCHAR(64) COMMENT '字段显示别名，多个以,分隔',
  type            VARCHAR(16) COMMENT '字段数据类型',
  length          INTEGER COMMENT '字段长度',
  `rank`          INTEGER DEFAULT 0 COMMENT '字段排序',
  `default_value` VARCHAR(32) COMMENT '字段默认值',
  extra           VARCHAR(8) COMMENT '字段额外信息, 是否主键，是否唯一，是否非空，是否0填充...',
  remark          VARCHAR(256) COMMENT '字段备注信息',
  table_id        VARCHAR(64) COMMENT '所属数据表',
  database_id     VARCHAR(64) COMMENT '所属数据库',
  datasource_id   VARCHAR(64) COMMENT '所属数据源',
  FOREIGN KEY (table_id) REFERENCES md_table (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (database_id) REFERENCES md_database (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (datasource_id) REFERENCES md_datasource (id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '字段表';

########################################################################################################################

DROP TABLE IF EXISTS std_topic;
CREATE TABLE IF NOT EXISTS std_topic
(
  id          INTEGER AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(32)            NOT NULL UNIQUE COMMENT '主题',
  description VARCHAR(128) COMMENT '主题说明'
) COMMENT '标准主题表';

DROP TABLE IF EXISTS std_catalog;
CREATE TABLE IF NOT EXISTS std_catalog
(
  id          INTEGER AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(32)            NOT NULL COMMENT '标准类别名称',
  parent_id   INTEGER                NOT NULL DEFAULT 0 COMMENT '上级类别ID',
  description VARCHAR(256) COMMENT '类别说明',
  topic_id    INTEGER COMMENT '类别所属的主题ID'
) COMMENT '标准类别表';

DROP TABLE IF EXISTS std_metrics;
CREATE TABLE IF NOT EXISTS std_metrics
(
  id               INTEGER AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY,
  name             VARCHAR(32)            NOT NULL COMMENT '指标名称',
  code             VARCHAR(32)            NOT NULL UNIQUE COMMENT '指标编码',
  label            VARCHAR(32)            NOT NULL COMMENT '指标显示名称',
  alias            VARCHAR(64)            NOT NULL COMMENT '指标别名，多个用,分隔',
  status           TINYINT COMMENT '指标状态, 正常/禁用/删除',
  frequency        INTEGER COMMENT '统计频率，1年，2季，4半年, 8月，16周，32日',
  according        VARCHAR(128) COMMENT '制定依据',
  data_type        VARCHAR(16) COMMENT '数据类型',
  data_length      INTEGER COMMENT '数据长度',
  topic_id         INTEGER COMMENT '主题ID',
  system_id        INTEGER COMMENT '所属系统ID',
  catalog_id       INTEGER COMMENT '指标类别ID',
  department_id    INTEGER COMMENT '所属部门ID',
  business_define  VARCHAR(256) COMMENT '业务定义',
  business_desc    VARCHAR(512) COMMENT '业务说明',
  technique_define VARCHAR(512) COMMENT '技术定义',
  supervised       TINYINT DEFAULT 0 COMMENT '是否监管',
  effect_time      DATETIME COMMENT '生效日期',
  expire_time      DATETIME COMMENT '失效日期',
  description      VARCHAR(512) COMMENT '描述',
  version          VARCHAR(32) COMMENT '所属版本',
  created_by       INTEGER COMMENT '创建人',
  created_at       DATETIME  COMMENT '创建时间',
  updated_by       INTEGER COMMENT '修改人',
  updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '修改时间',
  deleted_by       INTEGER COMMENT '删除人',
  deleted_at       DATETIME COMMENT '删除时间'
) COMMENT '指标标准表';


