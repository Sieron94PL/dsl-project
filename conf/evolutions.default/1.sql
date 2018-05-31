# User schema

# --- !Ups

create table `user` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`first_name`
 TEXT NOT NULL,`last_name` TEXTNOT NULL,`mobile` BIGINT NOT NULL,
 `email` TEXT NOT NULL)

# --- !Downs

drop table `user`

CREATE TABLE `dsl_project`.`book` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NULL,
  `title` VARCHAR(45) NULL,
  `category` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_id`
    FOREIGN KEY ()
    REFERENCES `dsl_project`.`user` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_polish_ci;

