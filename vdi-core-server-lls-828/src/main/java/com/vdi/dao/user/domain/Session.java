/**   
 * Title: Session.java 
 * @Package com.crawler.pojo 
 * : Session.java 
 * @author david   
 * @date 2013-2-3 上午12:47:40 
 * @version 
 */
package com.vdi.dao.user.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * ClassName: Session :
 * 
 * @author david
 * @date 2013-2-3 上午12:47:40
 */
@Entity
public class Session {
	
	
	private String ticket;
	
	private String username;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idsession;
	private Date expire = EXPIRE_30;
	public static Date EXPIRE_30 = new Date(System.currentTimeMillis() + 1000 * 60 * 30);

	/**
	 * @return expire
	 */
	public Date getExpire() {
		return expire;
	}

	/**
	 * @param expire
	 *            the expire to set
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}



	/**
	 * @return ticket
	 */
	public String getTicket() {
		return ticket;
	}

	/**
	 * @param ticket
	 *            the ticket to set
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	public Integer getIdsession() {
		return idsession;
	}

	public void setIdsession(Integer idsession) {
		this.idsession = idsession;
	}


	
}
