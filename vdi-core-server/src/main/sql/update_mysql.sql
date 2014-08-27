ALTER TABLE user ADD storagecloudmanagerid INT       NULL                    COMMENT '个人存储所使用的虚拟平台的ID';
ALTER TABLE user ADD storageid             CHAR(127) NULL                    COMMENT '个人存储ID';
ALTER TABLE license ADD type INT(1) NOT NULL COMMENT '类型:0=默认/1=正式/2=试用';
ALTER TABLE license ADD `mode` int(1) NOT NULL COMMENT '模式:0=Ukey/1=单机/2=集群';
ALTER TABLE license ADD  `expire` int(5)  NOT NULL COMMENT '有效天数';

CREATE TABLE `licenseserver` (
  `idlicenseserver` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ip` varchar(20) NOT NULL COMMENT 'license sever ip',
  `port` varchar(10) NOT NULL COMMENT 'license sever 端口 ',
  PRIMARY KEY (`idlicenseserver`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
