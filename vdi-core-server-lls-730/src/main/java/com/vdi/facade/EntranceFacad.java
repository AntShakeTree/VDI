package com.vdi.facade;

import com.vdi.vo.req.LoginInfo;
import com.vdi.vo.req.TicketReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LoginResponse;

public interface EntranceFacad {

	LoginResponse loginSession(LoginInfo loginInfo);
	Header logoutSession(TicketReq req);
}
