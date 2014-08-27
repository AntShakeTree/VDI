package com.opzoon.vdi.core.controller.deamon;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.opzoon.vdi.core.facade.TaskFacade;
import com.opzoon.vdi.core.operation.AllocAndConnectDesktopOperation;

/**
 * 
 * @author maxiaochao
 *
 */
@Component
public class ClearTask {
	static Logger logger =Logger.getLogger(ClearTask.class);
	@Autowired TaskFacade taskFacade;
	/**
	 * 启动时自动执行此方法来清理会话.
	 */
	@SuppressWarnings("all")
	@Scheduled(fixedDelay = 1000  * 5)
	public void clearTask() {
		logger.debug("clearTask ...deamon");
	    taskFacade.getDatabaseFacade().update("delete from TaskEntity where para1=? and status>3 or error<>0 ",AllocAndConnectDesktopOperation.class.getName());
	    List<Object> objecs=   (List<Object>) taskFacade.getDatabaseFacade().findByNativeSQL("select iddesktop from desktopstatus t1 where NOT  EXISTS (select 1 from desktopstatusconnectionview t2 where t1.iddesktop=t2.iddesktop)");
	    String sql="select idsession from desktopstatusconnectionview where connected=0";
	    
	    for (Object ob: objecs) {
			if(ob!=null){
				taskFacade.getDatabaseFacade().execNativeSql("update desktopstatus set connected=0 where iddesktop="
						+ Integer.parseInt(ob+""));	
			}
		}
	   taskFacade.getDatabaseFacade().update("delete from Session where deleted<>?",0);
	    List<Object> seids=   (List<Object>) taskFacade.getDatabaseFacade().findByNativeSQL(sql);
	    
	    	for (Object ob: seids) {
	 			if(ob!=null){
//	 				taskFacade.getDatabaseFacade().execNativeSql("delete from  session where idsession="+Integer.parseInt(ob+""));
	 				taskFacade.getDatabaseFacade().execNativeSql("delete from connection where sessionid="+Integer.parseInt(ob+""));
	 			}
	 		}
	}
	@PostConstruct
	public void init(){
		   taskFacade.getDatabaseFacade().update("delete from TaskEntity where status>=?",3);
		   taskFacade.getDatabaseFacade().execNativeSql("delete from desktop where vmid is null");
	}
}
