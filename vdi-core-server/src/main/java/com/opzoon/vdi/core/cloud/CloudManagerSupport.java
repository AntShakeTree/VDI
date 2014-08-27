package com.opzoon.vdi.core.cloud;

import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.domain.PersistentMessage;
import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.appstatus.facade.impl.AppStatusServiceImpl;
import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.response.UsernameOrPasswordException;

public abstract class CloudManagerSupport implements CloudManager {

	
	public void clusterLogin(String baseUrl,Certificate certificate) throws Exception{
		AppStatusService appStatusService = new AppStatusServiceImpl();
		PersistentMessage message=new PersistentMessage();
		message.setId(baseUrl);
		message.setClassname(certificate.getProxyName());
		message.setMethod("login");
		message.setMessage(ParseJSON.getGSON().toJson(certificate));
		appStatusService.publishPersistentMessage(message);
		
	}

}
