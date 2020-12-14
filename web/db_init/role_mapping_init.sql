-- sensecloud.component definition

create TABLE `component` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '系统包含的组件ID，主键',
  `name` varchar(256) CHARACTER SET latin1 NOT NULL COMMENT '组件名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO sensecloud.component (id,name) VALUES
	 (1,'web'),
	 (2,'airflow'),
	 (3,'superset'),
	 (4,'clickhouse');


-- sensecloud.`role` definition

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID，主键',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `component_id` int(11) DEFAULT NULL COMMENT '角色所属组件ID',
  PRIMARY KEY (`id`),
  KEY `role_FK` (`component_id`),
  CONSTRAINT `role_FK` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO sensecloud.`role` (id,name,component_id) VALUES
	 (1,'DataDeveloper',1),
	 (2,'DataAnalyst',1),
	 (3,'PlatformAdmin',1),
	 (4,'ProductAdmin',1),
	 (5,'GitlabRepo',2),
	 (6,'Public',2),
	 (7,'Admin',2),
	 (8,'Viewer',2),
	 (9,'User',2),
	 (10,'Op',2),
	 (11,'Group',2),
	 (12,'Public',3),
	 (13,'Admin',3),
	 (14,'Alpha',3),
	 (15,'Gamma',3),
	 (16,'granter',3),
	 (17,'sql_lab',3),
	 (18,'Group',3),
	 (19,'Admin',4),
	 (20,'granter',4),
	 (21,'Group',4);

-- sensecloud.web_component_role_mapping definition

CREATE TABLE `web_component_role_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '映射关系ID，主键',
  `web_role_id` int(11) NOT NULL COMMENT 'Web系统角色ID',
  `component_role_id` int(11) NOT NULL COMMENT '其他系统角色ID',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用该映射关系，默认1，启用',
  PRIMARY KEY (`id`),
  KEY `web_component_role_mapping_FK` (`component_role_id`),
  KEY `web_component_role_mapping_FK_1` (`web_role_id`),
  CONSTRAINT `web_component_role_mapping_FK` FOREIGN KEY (`component_role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `web_component_role_mapping_FK_1` FOREIGN KEY (`web_role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO sensecloud.web_component_role_mapping (id,component_role_id,web_role_id,enabled) VALUES
	 (1,5,1,1),
	 (2,6,1,1),
	 (3,11,1,1),
	 (4,15,1,1),
	 (5,17,1,1),
	 (6,18,1,1),
	 (7,21,1,1),
	 (8,6,2,1),
	 (9,15,2,1),
	 (10,17,2,1),
	 (11,18,2,1),
	 (12,21,2,1),
	 (13,5,3,1),
	 (14,7,3,1),
	 (15,13,3,1),
	 (16,19,3,1),
	 (17,5,4,1),
	 (18,10,4,1),
	 (19,14,4,1),
	 (20,16,4,1),
	 (21,20,4,0);