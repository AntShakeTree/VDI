package com.opzoon.vdi.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 用户登录的会话.
 */
@Entity
public class Session implements Serializable {

	private static final long serialVersionUID = 1L;

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

	private Integer idsession;
	private int userid;
	private int logintype;
	private String ticket;
	private Date expire;
	private String password;
	private String username;
	private int deleted;
	private int invalidatingreasoncode;
	private String source;
	private String clienttype;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdsession() {
		return idsession;
	}

	public void setIdsession(Integer idsession) {
		this.idsession = idsession;
	}

	public String getClienttype() {
		return clienttype;
	}

	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
	}

	/**
	 * @return 用户ID.
	 */
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	/**
	 * @return 登录身份. 参考{@link Session#LOGIN_TYPE_ADMIN},
	 *         {@link Session#LOGIN_TYPE_VICE_ADMIN},
	 *         {@link Session#LOGIN_TYPE_USER}.
	 */
	public int getLogintype() {
		return logintype;
	}

	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}

	/**
	 * @return 登录令牌.
	 */
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * @return 有效截至时间.
	 */
	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Transient
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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


}
