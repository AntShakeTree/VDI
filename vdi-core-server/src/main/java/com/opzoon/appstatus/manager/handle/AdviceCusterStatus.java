package com.opzoon.appstatus.manager.handle;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusClusterDownException;
import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.appstatus.manager.NodeManager;

@Aspect
@Component("adviceCusterStatus")
public class AdviceCusterStatus {
	@Pointcut("execution(* com.opzoon.vdi.core.ws.ServicesImpl.*(..)) && !@annotation(org.junit.Ignore)")
	public void advice(){
		
	}
	@Autowired
	
	private AppStatusService appStatusService;
	@Autowired
	private AppStatusDao appStatusDao;
	@Before("advice()")
	public void checkCustuerStatus() throws AppstatusRestException{
		boolean isLost =appStatusService.isInCluster();
		if(isLost){
			String ip =NodeManager.getLocalNodeAddress();
			NodeReq nodeReq=new NodeReq();
			nodeReq.setNodeAddress(ip);
			Node nodedb =appStatusDao.findNode(nodeReq);
			nodedb.setNodeState(NodeState.RUNNING);
			nodedb.setClusterState(ClusterState.CLUSTER);
			appStatusDao.updateNode(nodedb);
		}else{
			throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusClusterDownException());
		}
	}
}
