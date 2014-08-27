package com.opzoon.appstatus.executor.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.executor.dao.TraceDao;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.domain.Trace;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.DatabaseFacade;
@Component("traceDao")
public class TraceDaoImpl implements TraceDao{
	static Logger loger =Logger.getLogger(TraceDaoImpl.class);
	@Autowired
	private DatabaseFacade databaseFacade;

	@Override
	public void saveTrace(Trace trace) {
		databaseFacade.persist(trace);
	}
	@Override
	public User findUser(int userid) {
		return databaseFacade.get(User.class, userid);
	}
	@Override
	public List<Trace> listByUser(int userid,PageRequest<Trace> req) {
		loger.debug("listByUser :====> list trace params:"+ParseJSON.toJson(req));
		try {
			return databaseFacade.listPageRequest(req);
		} catch (Exception e) {
			loger.error("TraceDaoImpl ==>listByUser ", e);
		}
		return null;
	}
	@Override
	public User findUser(String username) {
		// TODO Auto-generated method stub
		return (User) this.databaseFacade.findFirst("from User where username=?", username);
	}
	
}
