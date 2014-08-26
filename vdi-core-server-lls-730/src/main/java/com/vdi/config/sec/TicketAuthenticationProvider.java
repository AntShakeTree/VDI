/**   
 * Title: AuthenticationProvider.java 
 * @Package com.crawler.common.gsonBuilder 
 * : AuthenticationProvider.java 
 * @author david   
 * @date 2013-1-28 下午11:20:47 
 * @version 
 */
package com.vdi.config.sec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.vdi.dao.user.domain.User;
import com.vdi.service.user.SessionService;
import com.vdi.service.user.UserService;

/**
 * ClassName: AuthenticationProvider
 * :
 * @author david
 * @date 2013-1-28 下午11:20:47
 */
@Component("ticketAuthenticationProvider")
public class TicketAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private SessionService sessionService;
	@Autowired
	private UserService userService;
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String ticket = (String) authentication.getPrincipal();	
		String username = sessionService.getUsernameByTicket(ticket);
		if("".equals(username)){
			return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), null);
		}
		User user =(User)userService.loadUserByUsername(username);
		//保留session Expire逻辑
		if (user == null) {
			return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), null);
		}else{
			sessionService.setCurrentSession(user);
		}
		//获得所有权限
		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),user.getAuthorities());
	}
	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
