CREATE TABLE `lls`.`desktop` (
  `iddesktop` INT NOT NULL AUTO_INCREMENT,
  `vmname` VARCHAR(45) NULL,
  `vmid` VARCHAR(45) NULL,
  `poolid` INT NULL,
  `poolname` VARCHAR(45) NULL,
  PRIMARY KEY (`iddesktop`));
  
  CREATE TABLE `lls`.`user` (
  `iduser` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NULL,
  `password` VARCHAR(45) NULL,
  `enable` TINYINT NULL,
  PRIMARY KEY (`iduser`));
  
  CREATE TABLE `lls`.`authorities` (
  `idauthorities` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NULL,
  PRIMARY KEY (`idauthorities`));