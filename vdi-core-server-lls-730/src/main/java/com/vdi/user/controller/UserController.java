package com.vdi.user.controller;

import org.springframework.stereotype.Controller;

import com.vdi.facade.EnterFacade;
import com.vdi.vo.req.LoginInfo;
import com.vdi.vo.req.NullResponse;
import com.vdi.vo.res.LoginResponse;

/**
 * 
 * @author ant_shake_tree
 *
 */
@Controller
public class UserController {
//	public 
	private EnterFacade enterFacad;
	
	public LoginResponse loginSession(LoginInfo loginInfo) {
		
		return null;
	}
	

	public NullResponse logoutSession() {
		return null;
	}
}
