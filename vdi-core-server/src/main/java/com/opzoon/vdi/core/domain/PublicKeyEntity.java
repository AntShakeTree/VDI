package com.opzoon.vdi.core.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "publicKey")
@Entity
@Table(name = "publicKey")
public class PublicKeyEntity {

	/*
	 * 	CREATE TABLE `publicKey` (
				  `idpublickey` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
				  `content` VARCHAR(1024) NOT NULL COMMENT '执行类全名',
				  PRIMARY KEY (`idpublickey`)
				) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
				*/
	private int idpublickey;
	private String content;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdpublickey() {
		return idpublickey;
	}
	public void setIdpublickey(int idpublickey) {
		this.idpublickey = idpublickey;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
