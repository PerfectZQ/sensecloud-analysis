CREATE TABLE sensecloud.connector (
	id  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	name varchar(20) NULL COMMENT '连接器的名称',
	source_name varchar(100) NULL COMMENT '数据源名称',
	source_type varchar(10) NULL COMMENT '数据源类型：KAFKA；MYSQL_BINLOG',
	source_conf json NULL COMMENT '数据源配置信息',
	sink_name varchar(20) NULL COMMENT '数据汇聚地名称',
	sink_type varchar(20) NULL COMMENT '数据汇聚地类型',
	sink_conf json NULL COMMENT '数据汇聚地配置信息',
	create_by varchar(20) NULL COMMENT '记录创建人AD账号',
	create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
	update_by varchar(20) NULL COMMENT '记录最后一次更新的用户AD账号',
	update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP NULL COMMENT '最后一次更新时间',
	deleted INT DEFAULT 0 NULL COMMENT '记录是否已经被删除',
	delete_by varchar(20) DEFAULT 'dba' COMMENT '记录删除人AD',
	delete_time TIMESTAMP NULL DEFAULT NULL COMMENT '记录删除时间',
	CONSTRAINT connector_PK PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci
COMMENT='数据连接器表，存储用户创建的连接器记录';