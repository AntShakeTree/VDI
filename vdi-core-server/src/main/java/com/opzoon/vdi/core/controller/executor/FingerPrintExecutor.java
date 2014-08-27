/**   
* @Title: LicenseStartExcutor.java 
* Package com.opzoon.vdi.core.controller 
* Description: TODO(鐢ㄤ竴鍙ヨ瘽鎻忚堪璇ユ枃浠跺仛浠�箞) 
* @author David   
* @date 2013-11-11 涓嬪崍2:43:01 
* @version V1.0   
*/
package com.opzoon.vdi.core.controller.executor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.util.LicenseUtil;

/** 
 * ClassName: LicenseStartExcutor 
 * Description: TODO(杩欓噷鐢ㄤ竴鍙ヨ瘽鎻忚堪杩欎釜绫荤殑浣滅敤) 
 * @author david 
 * @date 2013-11-11 涓嬪崍2:43:01 
 *  
 */
public class FingerPrintExecutor extends ExecutorBase{

	private static final Logger log = LoggerFactory.getLogger(FingerPrintExecutor.class);
	/* (闈�Javadoc) 
	* <p>Title: execute</p> 
	* <p>Description: </p> 
	* @param task
	* @return 
	* @see com.opzoon.vdi.core.controller.executor.ExecutorBase#execute(com.opzoon.vdi.core.controller.TaskInfo) 
	*/
	@Override
	public ExecuteResult execute(TaskInfo task) {
		// TODO Auto-generated method stub
		log.info("receive fingerpring " + task.getPara3());
		if(task.getPara3() != null && !task.getPara3().isEmpty())
		{
			Set<String> hwSet = LicenseUtil.toHardwareSet(task.getPara3());
			
			LicenseUtil.hardwareInfoSet.addAll(hwSet);
			LicenseUtil.hardwareInfoInFileSet.addAll(hwSet);
			
			//save set into file
			String hwFileContent = LicenseUtil.getAllFileHardwareInfo();;
			try {
				Writer out = new OutputStreamWriter(new FileOutputStream(LicenseUtil.HARDWARE_FILE_PATH));
				FileCopyUtils.copy(hwFileContent, out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ExecuteResult result = new ExecuteResult();
		result.setErrorCode(0);
		return result;
	}

}
