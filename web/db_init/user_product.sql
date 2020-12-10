CREATE TABLE sensecloud.user_product (
	id INT auto_increment NOT NULL,
	user_id INT NOT NULL,
	product_id INT NOT NULL,
	CONSTRAINT user_product_PK PRIMARY KEY (id),
	CONSTRAINT user_product_FK FOREIGN KEY (product_id) REFERENCES sensecloud.product(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_product_FK_1 FOREIGN KEY (user_id) REFERENCES sensecloud.`user`(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
