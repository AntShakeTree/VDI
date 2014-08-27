/**   
* @Title: ReceiveMaxConnectCountExecutor.java 
* Package com.opzoon.vdi.core.controller 
* Description: TODO(用一句话描述该文件做什么) 
* @author David   
* @date 2013-11-11 下午3:42:24 
* @version V1.0   
*/
package com.opzoon.vdi.core.controller.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.ws.admin.LicenseMangement;

/** 
 * ClassName: ReceiveMaxConnectCountExecutor 
 * Description: 导入授权时接收MaxConnect参数
 * @author david 
 * @date 2013-11-11 下午3:42:24 
 *  
 */
public class MaxConnectCountExecutor extends ExecutorBase{

	private static final Logger log = LoggerFactory.getLogger(MaxConnectCountExecutor.class);
	/* (非 Javadoc) 
	* <p>Title: execute</p> 
	* <p>Description: </p> 
	* @param task
	* @return 
	* @see com.opzoon.vdi.core.controller.executor.ExecutorBase#execute(com.opzoon.vdi.core.controller.TaskInfo) 
	*/
	
	@Override
	public ExecuteResult execute(TaskInfo task) {
		// TODO Auto-generated method stub
		log.info("receive connect_Count " + task.getPara1());
		if(task.getPara1() != null && !task.getPara1().isEmpty())
		{
			int count = Integer.parseInt(task.getPara1());
			LicenseMangement.connect_count = count;
		}
		ExecuteResult result = new ExecuteResult();
		result.setErrorCode(0);
		return result;
	}

}
