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
public class Session {
	/**
	 * 登录身份: 全局管理员.
	 */
	public static final int LOGIN_TYPE_SUPER_ADMIN = 0x00;
	/**
	 * 登录身份: 组或组织单元管理员.
	 */
	public static final int LOGIN_TYPE_ADMIN = 0x01;
	/**
	 * 登录身份: 用户.
	 */
	public static final int LOGIN_TYPE_USER = 0x11;

	public static final int INVALIDATING_REASON_CODE_DEFAULT = 0x0;
	public static final int INVALIDATING_REASON_CODE_LOGGED_OUT = 0x1;
	public static final int INVALIDATING_REASON_CODE_TIMED_OUT = 0x2;
	public static final int INVALIDATING_REASON_CODE_KILLED = 0x3;
	public static final int INVALIDATING_REASON_CODE_KICKED = 0x4;
	public static final int INVALIDATING_REASON_CODE_USER_DELETED = 0x5;

	private int userid;
	private int logintype;
	private String password;
	private int invalidatingreasoncode;
	private String source;
	private String clienttype;

	
	private String ticket;
	
	private String username;
	
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



	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getLogintype() {
		return logintype;
	}

	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getInvalidatingreasoncode() {
		return invalidatingreasoncode;
	}

	public void setInvalidatingreasoncode(int invalidatingreasoncode) {
		this.invalidatingreasoncode = invalidatingreasoncode;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getClienttype() {
		return clienttype;
	}

	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
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
