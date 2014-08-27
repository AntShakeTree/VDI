package com.opzoon.vdi.core.util;

import com.opzoon.appstatus.domain.PersistentMessage;

/**
 *AppstatusPersistentMessageMonitor  持久消息 监听器
 * 
 * @author david
 * @version V0.2.1023（迭代3） Date：2013-11-08
 */
public interface AppstatusPersistentMessageMonitor {
	void monitor(PersistentMessage message);
}
