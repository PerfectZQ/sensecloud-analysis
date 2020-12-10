CREATE TABLE sensecloud.user_service (
	id INT auto_increment NOT NULL,
	user_id INT NOT NULL,
	service_id INT NOT NULL,
	CONSTRAINT user_service_PK PRIMARY KEY (id),
	CONSTRAINT user_service_FK FOREIGN KEY (service_id) REFERENCES sensecloud.service(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_service_FK_1 FOREIGN KEY (user_id) REFERENCES sensecloud.`user`(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
