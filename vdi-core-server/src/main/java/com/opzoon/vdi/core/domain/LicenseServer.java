package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**   create script
 * CREATE TABLE `licenseserver` (
  `idlicenseserver` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ip` varchar(20) NOT NULL COMMENT 'license sever ip',
  `port` varchar(10) NOT NULL COMMENT 'license sever 端口 ',
  PRIMARY KEY (`idlicense`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
 * @author zhanglu
 *
 */
/**
 * 
 * @author zhanglu
 *
 */
@XmlRootElement(name = "licenseserver")
@Entity
@Table(name = "licenseserver")
public class LicenseServer implements Serializable {

	private static final long serialVersionUID = 1L;
	private int idlicenseserver;
	private String ip;
	private String port;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdlicenseserver() {
		return idlicenseserver;
	}
	public void setIdlicenseserver(int idlicenseserver) {
		this.idlicenseserver = idlicenseserver;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

}
