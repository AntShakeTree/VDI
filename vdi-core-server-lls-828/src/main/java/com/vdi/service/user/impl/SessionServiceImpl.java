/**
 * Project Name:vdi-core-server-lls
 * File Name:SessionServiceImpl.java
 * Package Name:com.vdi.service.user.impl
 * Date:2014年8月8日下午4:21:39
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vdi.dao.user.SessionDao;
import com.vdi.dao.user.domain.Session;
import com.vdi.dao.user.domain.User;
import com.vdi.service.user.SessionService;
@Service("sessionService")
public class SessionServiceImpl implements SessionService{
	@Autowired
	private SessionDao sessionDao;
	private ThreadLocal<com.vdi.dao.user.domain.User> threadLocalCurrentSession;

	/**
	 * Title: SessionService.java saveSession
	 * :
	 * @return void
	 * @throws
	 */
	@Transactional(readOnly = false)
	public void saveSession(Session session) {
		if (session.getTicket() != null) {
			Session session2 = sessionDao.findOneByKey("ticket", session.getTicket());
			if (session2 == null) {
				this.sessionDao.save(session);
			} else {
				this.sessionDao.update(session2);
			}
		}
	}

	/**
	 * 获取当前用户的会话.
	 * 
	 * @return 当前用户的会话. 未登录则为null.
	 */
	public User getCurrentSession() {
		if (threadLocalCurrentSession == null) {
			return null;
		}
		return threadLocalCurrentSession.get();
	}

	/**
	 * 将用户会话作为当前用户的会话保存在线程本地变量中.
	 * 
	 * @param currentSession
	 *            用户会话
	 */
	public void setCurrentSession(User user) {
		if (threadLocalCurrentSession == null) {
			threadLocalCurrentSession = new ThreadLocal<User>();
		}
		threadLocalCurrentSession.set(user);
	}

	/**
	 * Title: SessionService.java findUsername
	 * :
	 * @param ticket
	 * @return
	 * @return Integer
	 * @throws
	 */
	@Transactional(readOnly = true)
	public String getUsernameByTicket(String ticket) {
		Session u = this.sessionDao.findOneByKey("ticket", ticket);
		if (u == null) {
			return "";
		}
		return u.getUsername();
	}

	/**
	 * Title: SessionService.java deleteSessionByTicket
	 * :
	 * @param ticket
	 * @return void
	 * @throws
	 */
	@Transactional(readOnly = false)
	public void deleteSessionByTicket(String ticket) {
		Session ses = this.sessionDao.findOneByKey("ticket", ticket);
		if (ses != null) {
			this.sessionDao.delete(ses);
		}
	}
}
