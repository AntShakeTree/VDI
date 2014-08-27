package com.opzoon.appstatus.executor.dao;

import java.util.List;

import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.domain.Trace;
import com.opzoon.vdi.core.domain.User;

public interface TraceDao {
	void saveTrace(Trace trace);

	User findUser(int userid);
	User findUser(String username);

	List<Trace> listByUser(int userid,PageRequest<Trace> req);
}
