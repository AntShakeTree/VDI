package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**   create script
 * CREATE TABLE `license` (
  `idlicense` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) NOT NULL COMMENT 'license鏂囦欢鍚嶇О',
  `content` varchar(500) NOT NULL COMMENT 'license鍔犲瘑瀛楃涓�,
  `createtime` varchar(50) NOT NULL COMMENT '瀵煎叆license鏃堕棿',
  `connectCount` varchar(5) COMMENT '',
  PRIMARY KEY (`idlicense`),
  UNIQUE KEY `uktitle` (`title`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
 * @author tanyunhua
 *
 */
/**
 * 
 * @author tanyunhua
 *
 */
@XmlRootElement(name = "license")
@Entity
@Table(name = "license")
public class License implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idlicense;
	private String title;		//license鏂囦欢鍚嶇О
	private String content;
	private String connectCount;	//鍐呭瓨涓疄鏃惰绠楄幏寰�
	private String createtime;
	//add by zhanglu 2014-07-07 start
	//`type` int(1) NOT NULL COMMENT '类型:0=默认/1=正式/2=试用',
	private int type = 0;
	//`mode` int(1) NOT NULL COMMENT '模式:0=Ukey/1=单机/2=集群',
	private int mode = 0;
	//`expire` int(5)  NOT NULL COMMENT '有效天数',
	private int expire = 0;
	//add by zhanglu 2014-07-07 end
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdlicense() {
		return idlicense;
	}
	public void setIdlicense(int idlicense) {
		this.idlicense = idlicense;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getConnectCount() {
		return connectCount;
	}
	public void setConnectCount(String connectCount) {
		this.connectCount = connectCount;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	//add by zhanglu 2014-07-07 start
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
	//add by zhanglu 2014-07-07 end
}
