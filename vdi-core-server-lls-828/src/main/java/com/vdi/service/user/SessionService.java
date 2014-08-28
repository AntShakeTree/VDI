/**   
 * Title: SessionService.java 
 * @Package com.crawler.service 
 * : SessionService.java 
 * @author david   
 * @date 2013-2-3 上午3:15:24 
 * @version 
 */
package com.vdi.service.user;

import com.vdi.dao.user.domain.Session;
import com.vdi.dao.user.domain.User;



/**
 * ClassName: SessionService
 * :
 * @author david
 * @date 2013-2-3 上午3:15:24
 */

public interface SessionService {
	public void deleteSessionByTicket(String ticket);
	public String getUsernameByTicket(String ticket); 
	public void saveSession(Session session);
	public User getCurrentSession();
	public void setCurrentSession(User user);
}
