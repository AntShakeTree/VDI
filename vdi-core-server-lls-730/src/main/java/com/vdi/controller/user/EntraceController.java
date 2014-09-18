package com.vdi.controller.user;

/**
 * 
 * @author ant_shake_tree
 *
 */
import static com.vdi.controller.BaseController.CONTEXT_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.facade.EntranceFacad;
import com.vdi.vo.req.LoginInfo;
import com.vdi.vo.req.TicketReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LoginResponse;
@Controller
public class EntraceController {
//	public 
	private @Autowired EntranceFacad enterFacad;
	@RequestMapping(value= "/loginSession",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	public @ResponseBody LoginResponse loginSession(@RequestBody LoginInfo loginInfo) {
		
		return enterFacad.loginSession(loginInfo);
	}
	
	@RequestMapping(value= "/logoutSession",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	public @ResponseBody Header logoutSession(@RequestBody TicketReq req) {
		return enterFacad.logoutSession(req);
	}
}
