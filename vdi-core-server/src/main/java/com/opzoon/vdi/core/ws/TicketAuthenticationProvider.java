package com.opzoon.vdi.core.ws;

import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_ADMIN;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_SUPER_ADMIN;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;

import java.util.LinkedList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.facade.transience.SessionFacade;

/**
 * 会话认证类.<br />
 * 使用请求头中的令牌查询数据库获取用户的角色.<br />
 * 引用此类的地方请见spring-cxf.xml.
 */
public class TicketAuthenticationProvider implements AuthenticationProvider {
  
  public static final ThreadLocal<Integer> INVALID_SESSION_REASON_CODE = new ThreadLocal<Integer>();
	
	private SessionFacade sessionFacade;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String ticket = (String) authentication.getPrincipal();
		List<GrantedAuthorityImpl> grantedAuthorities = new LinkedList<GrantedAuthorityImpl>();
    Session session = sessionFacade.findSession(ticket);
    if (session == null || session.getDeleted() != 0) {
      if (session != null)
      {
       INVALID_SESSION_REASON_CODE.set(session.getInvalidatingreasoncode());
       }
       return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), grantedAuthorities);
     }
     Integer role = session.getLogintype();
		if (role == null) {
			return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), grantedAuthorities);
		}
		sessionFacade.renewSession(ticket);
		grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		if (numberEquals(role, LOGIN_TYPE_SUPER_ADMIN)) {
			grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_SUPER_ADMIN"));
			grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
		}
		if (numberEquals(role, LOGIN_TYPE_ADMIN)) {
			grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
		}
		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), grantedAuthorities);
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

}
