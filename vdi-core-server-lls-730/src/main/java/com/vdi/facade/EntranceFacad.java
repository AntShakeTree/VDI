package com.vdi.facade;

import org.springframework.security.access.prepost.PostAuthorize;

import com.vdi.vo.req.LoginInfo;
import com.vdi.vo.req.TicketReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LoginResponse;

public interface EntranceFacad {
	LoginResponse loginSession(LoginInfo loginInfo);
	@PostAuthorize("hasAuthority('ROLE_USER')")
	Header logoutSession(TicketReq req);
}
