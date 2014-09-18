package com.vdi.vo.res;

import java.io.Serializable;

/**
 * 令牌容器.
 */
public class TicketWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ticket;
	private int userid;
	private String username;
	
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}