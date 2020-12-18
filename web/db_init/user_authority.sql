CREATE TABLE sensecloud.user_authority (
	id INT auto_increment NOT NULL,
	user_id INT NOT NULL,
	role_id INT NOT NULL,
	product_id INT NOT NULL,
	CONSTRAINT user_authority_PK PRIMARY KEY (id),
	CONSTRAINT user_authority_FK FOREIGN KEY (user_id) REFERENCES sensecloud.`user`(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_authority_FK_1 FOREIGN KEY (role_id) REFERENCES sensecloud.`role`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT user_authority_FK_2 FOREIGN KEY (product_id) REFERENCES sensecloud.`product`(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 给用户 sre.bigdata 授予平台管理员权限
insert into sensecloud.user_authority (id, user_id, role_id, product_id) values (1, 1, 3, 1);
-- 给用户 dlink 授予产品线 dlink 的产品线管理员权限
insert into sensecloud.user_authority (id, user_id, role_id, product_id) values (2, 2, 4, 2);