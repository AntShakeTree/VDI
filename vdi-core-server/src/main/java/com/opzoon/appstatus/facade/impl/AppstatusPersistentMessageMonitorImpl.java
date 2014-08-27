package com.opzoon.appstatus.facade.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.opzoon.appstatus.domain.PersistentMessage;
import com.opzoon.appstatus.facade.AppstatusPersistentMessageMonitor;

@Service("appstatusPersistentMessageMonitor")
public class AppstatusPersistentMessageMonitorImpl implements
		AppstatusPersistentMessageMonitor {
	static Logger log =Logger.getLogger(AppstatusPersistentMessageMonitorImpl.class);
	@Override
	public void monitor(PersistentMessage message) {
		log.info("=============appstatusPersistentMessageMonitor.monitor()=============="+message+"=================================");
		try {
			Object object = Class.forName(message.getClassname()).newInstance();
			List<Object> os = message.getParameters();
			Class<?>[] clazzs = new Class<?>[os.size()];
			for (int i = 0; i < clazzs.length; i++) {
				clazzs[i] = os.get(i).getClass();
			}
			Method m = Class.forName(message.getClassname()).getMethod(
					message.getMethod(), clazzs);
			m.invoke(object, os.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
