package com.vdi.facade.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.common.ErrorCode;
import com.vdi.common.ExceptionHandle;
import com.vdi.common.Session;
import com.vdi.common.Utils;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.User;
import com.vdi.facade.EntranceFacad;
import com.vdi.vo.req.LoginInfo;
import com.vdi.vo.req.TicketReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LoginResponse;
import com.vdi.vo.res.TicketWrapper;
@Service
public class EntranceFacadImpl implements EntranceFacad {
	private @Autowired DomainDao domainDao;
	private @Autowired UserDao userDao;
	@Override
	public  LoginResponse loginSession(LoginInfo loginInfo) {
		Assert.notNull(loginInfo);
		Assert.notNull(loginInfo.getDomainguid());
		Domain domain =domainDao.get(Domain.class, loginInfo.getDomainguid());
		LoginResponse response =new LoginResponse();
		User user =new User();
		if(domain.getDomaintype()==Domain.DOMAIN_TYPE_LOCAL){
			//user.setPassword(loginInfo.getPassword());
			user.setUsername(loginInfo.getUsername());
			user.setDomain(domain);
			List<User> users =userDao.listRequest(user);
			if(users==null){
				response.getHead().setError(ExceptionHandle.err.error((ErrorCode.INVALID_USERNAME)));
				return response;
			}
			if(users.size()==0){
				response.getHead().setError(ExceptionHandle.err.error((ErrorCode.INVALID_USERNAME)));
				return response;
			}
			User dao=users.get(0);
			if(!dao.getPassword().equals(loginInfo.getPassword())){
				response.getHead().setError(ExceptionHandle.err.error((ErrorCode.INVALID_USERNAME)));
				return response;
			}
			user=dao;
		}
		
		String ticket =Utils.generalTicket();
		Session.setCache(ticket,user,30l,TimeUnit.MINUTES);
		TicketWrapper body=new TicketWrapper();
		body.setTicket(ticket);
		body.setUserid(user.getIduser());
		body.setUsername(user.getUsername());
		response.setBody(body);
		return response;
	}
	@Override
	public Header logoutSession(TicketReq req) {
		Header header =new Header();
		header.setError(0);
		Session.removeCache(req.getTicket());
		return header;
	}
}
