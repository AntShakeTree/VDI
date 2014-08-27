package com.opzoon.vdi.core.cloud;
//
import java.util.List;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.request.SetHostnameRequest;
import com.opzoon.ohvc.response.ResourceNotMeetException;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.vdi.core.domain.Template;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.domain.Volume;

/**
 * 通用的ADICore 接口
 * 
 * @author morigen,maxiaochao
 * 
 * @version V04 2012-09-05
 * 
 */
public interface CloudManager {

	/**
	 * 登陆并认证
	 * 
	 * @param username
	 * @param password
	 * @param domain
	 * @return
	 * @throws Exception
	 * @TODO UsernameOrPasswordException
	 */
	void login(String username, String password, String domain)
			throws Exception,UsernameOrPasswordException;

	/**
	 * 
	 * CloudManager.exitLogin()
	 * 
	 * @throws Exception
	 * @return void Author：maxiaochao 2012-9-20 下午2:47:40
	 */
	void exitLogin() throws Exception;

	/**
	 * 查询所有模板
	 * 
	 * @param authentication
	 * @return
	 * @throws Exception
	 */
	List<Template>  listTemplates() throws Exception;

	/**
	 * 设置URL
	 * 
	 * @param url
	 *            baseUrl
	 * @return this
	 */
	void setBaseUrl(String baseUrl);

	/**
	 * 
	 * CloudManager.startVM()
	 * 
	 * @param vmId
	 * @return
	 * @throws Exception
	 * @return Job<String> Author：maxiaochao 2012-9-20 下午2:45:19
	 */
	Job<?> startVM(String vmId) throws Exception;

	/**
	 * 
	 * CloudManager.stopVM()
	 * 
	 * @param vmId
	 * @return
	 * @throws Exception
	 * @return Job<String> Author：maxiaochao 2012-9-20 下午2:45:23
	 */
	Job<?> stopVM(String vmId) throws Exception;

	/**
	 * 
	 * CloudManager.rebootVM()
	 * 
	 * @param vmId
	 * @return
	 * @throws Exception
	 * @return Job<String> Author：maxiaochao 2012-9-20 下午2:45:29
	 */

	Job<?> rebootVM(String vmId) throws Exception;

	/**
	 * 
	 * CloudManager.destroyVM()
	 * 
	 * @param vmId
	 * @return
	 * @throws Exception
	 * @return Job<String> Author：maxiaochao 2012-9-20 下午2:45:33
	 */
	Job<?> destroyVM(String vmId) throws Exception;

	// void queryJobStatus(Job<?> job) throws Exception;
	/**
	 * 
	 * CloudManager.getVM()
	 * 
	 * @param vmId
	 * @return
	 * @throws Exception
	 * @return VMInstance Author：maxiaochao 2012-9-20 下午2:45:53
	 */
	VMInstance getVM(String vmId) throws Exception;

	/**
	 * 
	 * CloudManager.cloneVM()
	 * 
	 * @param templateId
	 * @param nameOfNewVM
	 * @return
	 * @throws Exception
	 * @return Job<VMInstance> Author：maxiaochao 2012-9-20 下午2:45:49
	 */
	public Job<VMInstance> cloneVM(String templateId, String nameOfNewVM,boolean link)
			throws Exception,ResourceNotMeetException;

	/**
	 * 
	 * CloudManager.queryJobStatus()
	 * 
	 * @param job
	 * @throws Exception
	 * @return void Author：maxiaochao 2012-9-20 下午2:45:41
	 */
	<T> void queryJobStatus(Job<T> job) throws Exception;

	/**
	 * 查询vm的RDP状态是否已经启动。
	 * @param vmId
	 * @return
	 * @throws Exception
	 */
	public boolean getRdpStatus(String vmId) throws Exception;
	
	/**
	 * 设置主机名
	 * @param vmId
	 * @return
	 * @throws Exception
	 */
	public Job<SetHostnameRequest> setHostname(String vmId,String hostname,Integer type,
			String account,String password, boolean restart) throws Exception ;
	
	/**
	 * 从guest os内部关机 需要agent的支持
	 * @param vmId
	 * @return
	 * @throws Exception
	 */
	public void shutdownSystem(String vmId)throws Exception;
	
	/**
	 * 从guest os内部重启 需要agent的支持
	 * @param vmId
	 * @return
	 * @throws Exception
	 */
	public Job<String> restartSystem(String vmId)throws Exception;

	Job<String> joinDomain(String vmid, String domainname, String domainbinddn,
			String domainbindpass, boolean restart) throws Exception;

	Job<String> joinWorkgroup(String vmid, String workgroupname, String account, String password, boolean restart) throws Exception;

	Job<String> createUser(String vmid, String username, String password) throws Exception;

	Job<String> updateUserPassword(String vmid, String username, String password) throws Exception;

	Job<String> deleteUser(String vmid, String username) throws Exception;

	Job<String> deleteUserProfile(String vmid, String domain, String username) throws Exception;
	
	// Stub
	void deleteVolume(String storageid) throws Exception;

	// Stub
	Job<?> eraseVolume(String storageid) throws Exception;

	// Stub
	List<Volume> listVolumes(String volumeIdForQuerying) throws Exception;

	// Stub
	Job<?> detachVolume(String storageid) throws Exception;

	// Stub
	Job<?> attachVolume(String storageid, String vmid) throws Exception;

	// Stub
	Job<?> createVolume(String format, int size) throws Exception;
	//
	Job<?> resetVM(String templateId,String vmid) throws Exception;
	
	Job<String> logOff(String vmid, String domainname, String username) throws Exception;
}
