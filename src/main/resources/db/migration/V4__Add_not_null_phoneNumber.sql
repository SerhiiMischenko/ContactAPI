ALTER TABLE `Contact`.`contacts` 
CHANGE COLUMN `first_name` `first_name` VARCHAR(50) NULL ,
CHANGE COLUMN `last_name` `last_name` VARCHAR(50) NULL ,
CHANGE COLUMN `phone_number` `phone_number` VARCHAR(20) NOT NULL ;