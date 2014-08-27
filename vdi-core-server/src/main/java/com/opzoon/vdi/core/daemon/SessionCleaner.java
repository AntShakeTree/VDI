package com.opzoon.vdi.core.daemon;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.facade.UserFacade;

/**
 * 过期会话自动清理类.
 */
public class SessionCleaner {
	  private static Logger logger = Logger.getLogger(SessionCleaner.class);

	private UserFacade userFacade;

	/**
	 * 启动时自动执行此方法来清理会话.
	 */
	@Scheduled(fixedDelay = 1000 * 60 * 10)
	public void run() {
		logger.debug("SessionCleaner ...deamon");
		userFacade.cleanExpiredSessions();
	}

	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

}
