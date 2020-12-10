CREATE TABLE sensecloud.user_role (
	id INT auto_increment NOT NULL,
	user_id INT NOT NULL,
	role_id INT NOT NULL,
	CONSTRAINT user_role_PK PRIMARY KEY (id),
	CONSTRAINT user_role_FK FOREIGN KEY (user_id) REFERENCES sensecloud.`user`(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_role_FK_1 FOREIGN KEY (role_id) REFERENCES sensecloud.`role`(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;