package com.opzoon.vdi.core.ws;

import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.LinkedList;
import java.util.List;

import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.facade.UserFacade;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.vo.entrance.AuthenticationMethod;
import com.opzoon.vdi.core.ws.vo.entrance.AuthenticationMethodParamDef;
import com.opzoon.vdi.core.ws.vo.entrance.ListAvailableAuthenticationMethodsResponse;
import com.opzoon.vdi.core.ws.vo.entrance.LoginInfo;
import com.opzoon.vdi.core.ws.vo.entrance.LoginResponse;
import com.opzoon.vdi.core.ws.vo.entrance.NewPasswordWrapper;
import com.opzoon.vdi.core.ws.vo.entrance.TicketWrapper;

/**
 * 用户登录/登出业务实现.
 */
public class Entrance {
	
	private UserFacade userFacade;
	
	private List<AuthenticationMethod> authenticationMethods;
	
	public Entrance()
	{
    // FIXME
	  authenticationMethods = new LinkedList<AuthenticationMethod>();
	  
    AuthenticationMethod defaultMethod = new AuthenticationMethod();
    defaultMethod.setMethodname("simple");
    defaultMethod.setMethodversion("1.0");
    List<AuthenticationMethodParamDef> defaultMethodParams = new LinkedList<AuthenticationMethodParamDef>();
    AuthenticationMethodParamDef defaultMethodParamUsername = new AuthenticationMethodParamDef();
    defaultMethodParamUsername.setParamname("username");
    defaultMethodParamUsername.setParamtype(3);
    defaultMethodParams.add(defaultMethodParamUsername);
    AuthenticationMethodParamDef defaultMethodParamPassword = new AuthenticationMethodParamDef();
    defaultMethodParamPassword.setParamname("password");
    defaultMethodParamPassword.setParamtype(4);
    defaultMethodParams.add(defaultMethodParamPassword);
    defaultMethod.setParams(defaultMethodParams);
    authenticationMethods.add(defaultMethod);
    
    AuthenticationMethod standardRSAMethod = new AuthenticationMethod();
    standardRSAMethod.setMethodname("simple+dynamiccode");
    standardRSAMethod.setMethodversion("0.1");
    List<AuthenticationMethodParamDef> defaultRSAMethodParams = new LinkedList<AuthenticationMethodParamDef>();
    defaultRSAMethodParams.add(defaultMethodParamUsername);
    defaultRSAMethodParams.add(defaultMethodParamPassword);
    AuthenticationMethodParamDef defaultRSAMethodParamDynamiccode = new AuthenticationMethodParamDef();
    defaultRSAMethodParamDynamiccode.setParamname("dynamiccode");
    defaultRSAMethodParamDynamiccode.setParamtype(2);
    defaultRSAMethodParams.add(defaultRSAMethodParamDynamiccode);
    standardRSAMethod.setParams(defaultRSAMethodParams);
    authenticationMethods.add(standardRSAMethod);
	}
	
	public ListAvailableAuthenticationMethodsResponse listAvailableAuthenticationMethods()
	{
		ListAvailableAuthenticationMethodsResponse response = new ListAvailableAuthenticationMethodsResponse();
		response.setBody(authenticationMethods);
//		userFacade.aaa();
		return response;
	}

	/**
	 * 选择适当的用户身份登录.
	 * 
	 * @param loginInfo 登录信息.
	 * @return 登录响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#UNAUTHORIZED}: 无法登录.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足以用管理员登录.
	 */
	public LoginResponse loginSession(LoginInfo loginInfo) {
		LoginResponse response = new LoginResponse();
		if (numberNotEquals(loginInfo.getLogintype(), 0) && numberNotEquals(loginInfo.getLogintype(), 1)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
	  // FIXME delete it.
		if (loginInfo.getDomainguid() == null)
    {
		  loginInfo.setDomainguid("");
    }
		int[] errorContainer = new int[1];
		Session session = userFacade.login(
		    authenticationMethods,
				loginInfo.getParams(),
				loginInfo.getDomainguid(),
				numberEquals(loginInfo.getLogintype(), 0),
				errorContainer);
		if (session == null) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		TicketWrapper ticketWrapper = new TicketWrapper();
		ticketWrapper.setTicket(session.getTicket());
		ticketWrapper.setUserid(session.getUserid());
		ticketWrapper.setUsername(session.getUsername());
		response.setBody(ticketWrapper);
		return response;
	}

	/**
	 * 注销用户.
	 * 
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 */
	public NullResponse logoutSession() {
		userFacade.logout();
		return new NullResponse();
	}

	public NullResponse updatePassword(NewPasswordWrapper newPasswordWrapper) {
		NullResponse response = new NullResponse();
		if (!userFacade.checkPassword(newPasswordWrapper.getNewpassword())) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = userFacade.updatePassword(
				newPasswordWrapper.getOldpassword(),
				newPasswordWrapper.getNewpassword());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

}
