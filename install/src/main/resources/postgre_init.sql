CREATE DATABASE dmp;

########################################################################################################################

CREATE SCHEMA IF NOT EXISTS org;

CREATE TABLE IF NOT EXISTS org.user_account
(
  id       SERIAL PRIMARY KEY NOT NULL,
  username VARCHAR(64) NOT NULL UNIQUE ,
  password VARCHAR(128) NOT NULL,
  salt     VARCHAR(64) NOT NULL,
  realname VARCHAR(32),
  email    VARCHAR(64),
  mobile   VARCHAR(13),
  status   SMALLINT(1) DEFAULT 0
);
COMMENT
ON
TABLE
org
.
user_account
is
'用户账户表';
COMMENT
ON
COLUMN
org
.
user_account
.
id
'自增ID'
COMMENT
ON
COLUMN
org
.
user_account
.
status
'账户状态：0-正常，1-锁定，2-注销'

CREATE TABLE IF NOT EXISTS org.user_profile
(
  id       INTEGER PRIMARY KEY REFERENCES org.user_account ('id') COMMENT '用户ID',
  gender   BOOLEAN COMMENT '用户性别',
  address  VARCHAR(128) COMMENT '用户住址',
  usercode VARCHAR(32) COMMENT '人员编号'
) COMMENT '用户详情表';

CREATE TABLE IF NOT EXISTS org.role
(
  id            SERIAL NOT NULL UNIQUE PRIMARY KEY COMMENT '自增角色ID',
  name          VARCHAR(32) NOT NULL ,
  department_id INTEGER COMMENT '角色所属部门',
  description   VARCHAR(256) COMMENT '角色说明'
) COMMENT '用户角色表';


CREATE TABLE IF NOT EXISTS org.department
(
  id           SERIAL      NOT NULL UNIQUE PRIMARY KEY COMMENT '部门ID',
  name         VARCHAR(32) NOT NULL,
  chief_leader INTEGER REFERENCES org.user_account ('id') COMMENT '部门正领导',
  vice_leader  INTEGER REFERENCES org.user_account ('id') COMMENT '部门副领导',
  description  VARCHAR(256) COMMENT '部门说明'
) COMMENT '部门表';

CREATE TABLE IF NOT EXISTS org.resource
(
  id          SERIAL NOT NULL UNIQUE PRIMARY KEY ,
  name        VARCHAR(64) UNIQUE,
  type        VARCHAR(8) COMMENT '资源类型：MENU/BUTTON/API',
  code        VARCHAR(32) COMMENT '资源编码',
  parent_id   INTEGER ,
  uri         VARCHAR(128),
  method      VARCHAR(8),
  status      SMALLINT(1) COMMENT '资源状态：NORMAL/BLOCKED',
  description VARCHAR(128)
) COMMENT '资源表';

CREATE TABLE IF NOT EXISTS org.r_user_role
(
  user_id INTEGER REFERENCES org.user_account ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  role_id INTEGER REFERENCES org.role ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY (user_id, role_id)
) COMMENT '用户角色表';

CREATE TABLE IF NOT EXISTS org.r_user_department
(
  user_id       INTEGER REFERENCES org.user_account ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  department_id INTEGER REFERENCES org.department ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY (user_id, department_id)
) COMMENT '用户部门关联表';

CREATE TABLE IF NOT EXISTS org.r_role_department
(
  role_id       INTEGER REFERENCES org.role ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  department_id INTEGER REFERENCES org.department ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY (role_id, department_id)
) COMMENT '角色部门关联表';

CREATE TABLE IF NOT EXISTS org.r_role_resource
(
  role_id     INTEGER REFERENCES org.role ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  resource_id INTEGER REFERENCES org.resource ('id') ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY (role_id, resource_id)
) COMMENT '角色资源关联表';

########################################################################################################################
CREATE SCHEMA IF NOT EXISTS web;

CREATE TABLE IF NOT EXISTS web.md_catalog
(
  id        SERIAL      NOT NULL UNIQUE PRIMARY KEY,
  name      VARCHAR(32) NOT NULL UNIQUE COMMENT '系统类别名称',
  parent_id INTEGER     NOT NULL DEFAULT 0 COMMENT '上级类别',
  'order'   SMALLINT             DEFAULT 0 COMMENT '类别次序',
  remark    VARCHAR(256) COMMENT '系统类别说明'
) COMMENT '系统类别表';

CREATE TABLE IF NOT EXISTS web.md_system
(
  id         SERIAL      NOT NULL UNIQUE PRIMARY KEY,
  name       VARCHAR(32) NOT NULL UNIQUE COMMENT '系统名称',
  catalog_id INTEGER REFERENCES web.md_catalog ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属类别',
  remark     VARCHAR(256) COMMENT '系统说明'
) COMMENT '系统表';

CREATE TABLE IF NOT EXISTS web.md_datasource
(
  id          SERIAL      NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(32) NOT NULL UNIQUE,
  label       VARCHAR(32) COMMENT '数据源显示名称',
  type        VARCHAR(8) COMMENT '数据源类型',
  source      VARCHAR(32) COMMENT '数据源来源',
  status      SMALLINT(1) COMMENT '数据源状态',
  host        VARCHAR(128) COMMENT '数据源连接URL',
  port        INTEGER(6) COMMENT '数据源连接端口',
  user        VARCHAR(16) COMMENT '数据源连接用户名',
  pass        VARCHAR(32) COMMENT '数据源连接密码',
  description VARCHAR(256) COMMENT '数据源说明',
  system_id   INTEGER REFERENCES web.md_system ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属系统' COMMENT '数据源所属系统',
  created_by  INTEGER COMMENT '数据源添加人',
  created_at  TIMESTAMP COMMENT '数据源添加时间',
  updated_by  INTEGER COMMENT '数据源信息修改人',
  updated_at  TIMESTAMP COMMENT '数据源信息修改时间',
  deleted_by  INTEGER COMMENT '数据源删除人',
  deleted_at  INTEGER COMMENT '数据源删除时间'
) COMMENT '数据源表';

CREATE TABLE IF NOT EXISTS web.md_database
(
  id            BIGINT      NOT NULL UNIQUE PRIMARY KEY,
  name          VARCHAR(32) NOT NULL COMMENT '数据库名称 catalog',
  label         VARCHAR(32) COMMENT '数据库显示名称',
  remark        VARCHAR(256) COMMENT '数据库备注信息',
  datasource_id INTEGER REFERENCES web.md_datasource ('id') ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '数据库表';

CREATE TABLE IF NOT EXISTS web.md_table
(
  id            BIGINT      NOT NULL UNIQUE PRIMARY KEY,
  name          VARCHAR(32) NOT NULL COMMENT '表名',
  label         VARCHAR(32) COMMENT '表显示名称',
  'schema'      VARCHAR(32) COMMENT '表所属schema',
  'order'       INTEGER DEFAULT 0 COMMENT '表排序',
  primary_key   VARCHAR(32) COMMENT '表主键字段名称，若为联合主键则为主键逗号分隔值',
  ddl           VARCHAR(1024) COMMENT '建表语句',
  remark        VARCHAR(256) COMMENT '表备注信息',
  database_id   BIGINT REFERENCES web.md_database ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属数据库',
  datasource_id INTEGER REFERENCES web.md_datasource ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属数据源'
) COMMENT '数据表表';

CREATE TABLE IF NOT EXISTS web.md_column
(
  id            BIGINT      NOT NULL UNIQUE PRIMARY KEY,
  name          VARCHAR(32) NOT NULL COMMENT '字段名',
  label         VARCHAR(32) COMMENT '字段显示名称',
  type          VARCHAR(16) COMMENT '字段数据类型',
  length        INTEGER COMMENT '字段长度',
  'order'       INTEGER DEFAULT 0 COMMENT '字段排序',
  'default'     VARCHAR(32) COMMENT '字段默认值',
  extra         VARCHAR(8) COMMENT '字段额外信息, 是否主键，是否唯一，是否非空，是否0填充...',
  remark        VARCHAR(256) COMMENT '字段备注信息',
  table_id      BIGINT REFERENCES web.md_table ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属数据表',
  database_id   BIGINT REFERENCES web.md_database ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属数据库',
  datasource_id INTEGER REFERENCES web.md_datasource ('id') ON UPDATE CASCADE ON DELETE CASCADE COMMENT '所属数据源'
) COMMENT '字段表';

CREATE TABLE IF NOT EXISTS web.topic
(
  id          SERIAL      NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(32) NOT NULL UNIQUE COMMENT '主题',
  description VARCHAR(128) COMMENT '主题说明'
) COMMENT '标准主题表';

CREATE TABLE IF NOT EXISTS web.std_catalog
(
  id          SERIAL      NOT NULL UNIQUE PRIMARY KEY,
  name        VARCHAR(32) NOT NULL COMMENT '标准类别名称',
  parent_id   INTEGER     NOT NULL DEFAULT 0 COMMENT '上级类别ID',
  description VARCHAR(256) COMMENT '类别说明',
  topic_id    INTEGER COMMENT '类别所属的主题ID'
) COMMENT '标准类别表';

CREATE TABLE IF NOT EXISTS web.std_metrics
(
  id               SERIAL      NOT NULL UNIQUE PRIMARY KEY,
  name             VARCHAR(32) NOT NULL COMMENT '指标名称',
  code             VARCHAR(32) NOT NULL UNIQUE COMMENT '指标编码',
  label            VARCHAR(32) NOT NULL COMMENT '指标显示名称',
  alias            VARCHAR(64) NOT NULL COMMENT '指标别名，多个用,分隔',
  status           SMALLINT COMMENT '指标状态',
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
  supervised       BOOLEAN DEFAULT FALSE COMMENT '是否监管',
  version          VARCHAR(32) COMMENT '所属版本',
  effect_time      DATETIME COMMENT '生效日期',
  expire_time      DATETIME COMMENT '失效日期',
  description      VARCHAR(512) COMMENT '描述'
) COMMENT '指标标准表';
