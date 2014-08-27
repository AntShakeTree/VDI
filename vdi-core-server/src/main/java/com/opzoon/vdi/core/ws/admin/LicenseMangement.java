package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_CLUSTER_TYPE;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_DUPLICATE;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_ENCRYPT_ERROR;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_FINGER_ERROR;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_NO_PUBLIC_KEY;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_SERVER_DOFIANL_ERR;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_SERVER_ERR;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.LICENSE_MORE_ERR;

import java.io.Serializable;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.vdi.core.controller.Controller;
import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.controller.executor.MaxConnectCountExecutor;
import com.opzoon.vdi.core.domain.License;
import com.opzoon.vdi.core.domain.LicenseServer;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.LicenseFacade;
import com.opzoon.vdi.core.facade.LicenseServerFacade;
import com.opzoon.vdi.core.quartz.UkeyMonitorQuartz;
import com.opzoon.vdi.core.util.LicenseServerUtil;
import com.opzoon.vdi.core.util.LicenseUtil;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;
/**
 * 
 * @author tanyunhua
 *
 */
public class LicenseMangement {
	//TODO锛氶粯璁�0涓苟鍙戣繛鎺ユ巿鏉冨浣曞疄鐜般�
	public static final int INIT_CONNECT_COUNT = 100;
	public static int connect_count = INIT_CONNECT_COUNT;
	public static int LICENSE_HOURS = 0;
	private LicenseFacade licenseFacade;
	private LicenseServerFacade licenseServerFacade;
	private AppStatusService appStatusService;
	private static final Logger log = LoggerFactory.getLogger(LicenseMangement.class);

	@Autowired
	private Controller controller;
	
	private int getMaxLicense(List<License> licenseList)
	{
		System.out.println("getMaxLicense start");
		if(licenseList == null || licenseList.size() <= 0)
			return INIT_CONNECT_COUNT;
		int max = 0;
		int count = 0;
		for(License license: licenseList)
		{
			//update by zhanglu 2014-07-08 start
//			String value = null;
			String[] value = new String[5];
			//update by zhanglu 2014-07-08 end
			if(license.getConnectCount() == null || license.getConnectCount().equals(""))
			{
				try {
					value = LicenseUtil.getValueFromContent(license.getContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				//update by zhanglu 2014-07-08 start
//				value = license.getConnectCount();
				System.out.println("license.getConnectCount()");
				value[1] = license.getConnectCount();
				//update by zhanglu 2014-07-08 end
			}
			//update by zhanglu 2014-07-08 start
//			if(value == null || value.equals(""))
			if(value[1] == null || value[1].equals(""))
			//update by zhanglu 2014-07-08 end
			{
				log.info("can not get the count from license: " + license.getIdlicense());
				continue;
			}
			
			try{
				//update by zhanglu 2014-07-08 start
//				count = Integer.parseInt(value);
				count = Integer.parseInt(value[1]);
				//update by zhanglu 2014-07-08 end
			}catch(Exception e)
			{
			}
			if(count > max)
				max = count;
		}
		if(max <= 0)
		{
			max = INIT_CONNECT_COUNT;
		}
		System.out.println("getMaxLicense end");
		return max;
	}
	
	public LicenseResponse importLicense(License license) {
		LicenseResponse response = new LicenseResponse();
		if(license == null || license.getContent() == null || license.getContent().equals("")
				|| license.getTitle() == null || license.getTitle().equals(""))
		{
			response.getHead().setError(BAD_REQUEST);
			return response;
		}

		//add by zhanglu 2014-08-06 bugID:3335 start
		List<License> listLicense = licenseFacade.listLicense(0, null);
		if (null != listLicense && "".equals(listLicense) && listLicense.size() > 0 ){
			boolean flg = false;
			for (License lis : listLicense){
				if (lis.getExpire() > 0){
					flg = true; 
				}else{
					licenseFacade.deleteLicense(lis.getIdlicense());
				}
				break;
			}
			if (flg){
				response.getHead().setError(LICENSE_MORE_ERR);
				return response;
			}
		}
		//add by zhanglu 2014-08-06 bugID:3335 end
		//update by zhanglu 2014-07-07 start
//		String value = null;
		String[] value = null;
		//update by zhanglu 2014-07-07 end
		try {
			value = LicenseUtil.getValueFromContent(license.getContent());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			response.getHead().setError(LICENSE_FINGER_ERROR);
			return response;
		}
		//update by zhanglu 2014-07-07 start
//		if(value == null)
		if(value[1] == null || value[2] == null || value[3] == null || value[4] == null)
		//update by zhanglu 2014-07-07 end
		{
			response.getHead().setError(LICENSE_NO_PUBLIC_KEY);
			return response;
		}
		//add by zhanglu 2014-07-11 start
		//集群的时候需要通信license server
		if ("2".equals(value[3])){
			LicenseServerUtil lu = new LicenseServerUtil();
			List<LicenseServer> list = licenseServerFacade.listLicenseServer(0, null) ;
			if (null != list && list.size() > 0){
				String ip = list.get(0).getIp();
				int port = Integer.parseInt(list.get(0).getPort());
				int res = lu.queryPermit(ip, port, LicenseServerUtil.PRO_NAME_VDI, value[0]);
				if (0 != res){
					response.getHead().setError(LICENSE_SERVER_DOFIANL_ERR);
					return response;
				}
			}else{
				response.getHead().setError(LICENSE_SERVER_ERR);
				return response;
			}
		}
		//add by zhanglu 2014-07-11 end
		//license閲嶅
		if(licenseFacade.haveLicense(license.getContent()))
		{
			response.getHead().setError(LICENSE_DUPLICATE);
			return response;
		}
		//add by zhanglu 2014-07-08 start
		int hours = Integer.parseInt(value[2])*24;
		license.setConnectCount(value[1]);
		license.setExpire(hours);
		license.setMode(Integer.parseInt(value[3]));
		license.setType(Integer.parseInt(value[4]));
		//add by zhanglu 2014-07-08 end
		license.setCreatetime(dateFormate.format(new Date()));
		license.setIdlicense(0);
		int error = 0;
		try {
			// update by zhanglu 2014-07-22 start
//			error = licenseFacade.createLicense(license);
			if ("0".equals(value[3])){
				//导入Ukey时先查看Ukey中有没有license
				String res = LicenseServerUtil.readUkey(LicenseServerUtil.LICENSE_FILE);
				if (null != res){
					JSONObject obj =  (JSONObject) JSONObject.wrap(res);
					if (value[0].equals(obj.getString("content"))){
						response.getHead().setError(LICENSE_DUPLICATE);
						return response;
					}
				}else{
					//把license写入Ukey
					JSONObject obj = new JSONObject();
					obj.put("content", value[0]);
					obj.put("hours", hours);
					obj.put("connectcount", value[1]);
					LicenseServerUtil.writeUkey(LicenseServerUtil.LICENSE_FILE,obj.toString());
				}
			}
			
			error = licenseFacade.createLicense(license);
			// update by zhanglu 2014-07-22 end
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		//update by zhanglu 2014-07-22 start
//		}
		} catch (JSONException e) {
			response.getHead().setError(CommonException.UNKNOWN);
			return response;
		}
		//update by zhanglu 2014-07-22 end
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		//update by zhanglu 2014-07-22 start
		//鏇存柊鍐呭瓨涓浉搴旂殑骞跺彂杩炴帴鎺у埗鏁帮紝涓嬫杩炴帴鐢熸晥
//		connect_count = getMaxLicense(licenseFacade.listLicense(0, null));
		if (!"0".equals(value[3])){
			connect_count = getMaxLicense(licenseFacade.listLicense(0, null));
		}else{
			connect_count = Integer.parseInt(value[1]);
		}
		//update by zhanglu 2014-07-22 end
		log.info("import license connect_count is " + connect_count);
		log.info("controller is " + controller);
		//add by zhanglu 2014-07-05 start
		if (0 == UkeyMonitorQuartz.isExpired) {
			UkeyMonitorQuartz.isExpired = -1;
		}
		//add by zhanglu 2014-07-05 end
		//add by zhanglu 2014-08-16 bugId:3342 start
		UkeyMonitorQuartz.setSystemTimer("0");
		//add by zhanglu 2014-08-16 bugId：3342 end
		TaskInfo task = new TaskInfo();
		task.setExecutorClass(MaxConnectCountExecutor.class);
		task.setPara1(String.format("%d", connect_count));
		task.setType(1);
		controller.sendTask(task);
		
		license.setContent(null);
		//update by zhanglu 2014-07-07 start
//		license.setConnectCount(value);
		license.setConnectCount(value[1]);
		LicenseMangement.LICENSE_HOURS = hours;
		//update by zhanglu 2014-07-07 end
		response.setBody(license);
		return response;
	}
	
	SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ListLicenseResponse listLicense(LicenseIdParam licenseParam) {
		ListLicenseResponse response = new ListLicenseResponse();
		if(licenseParam == null)
			licenseParam = new LicenseIdParam();

		List<License> licenseList = licenseFacade.listLicense(licenseParam.getIdlicense(), null);
		for(License license: licenseList)
		{
			//update by zhanglu 2014-07-08 start
//			String count = null;
			String[] value = null;
			//update by zhanglu 2014-07-08 end
			try {
				//update by zhanglu 2014-07-08 start
//				count = LicenseUtil.getValueFromContent(license.getContent());
				value = LicenseUtil.getValueFromContent(license.getContent());
				//update by zhanglu 2014-07-08 end
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//update by zhanglu 2014-07-08 start
//			license.setConnectCount(count);
			LicenseMangement.LICENSE_HOURS = Integer.parseInt(null == value[2] ? "0" : value[2]);
			license.setConnectCount(value[1]);
			//update by zhanglu 2014-07-08 end
			license.setContent(null);
		}

		ListLicense listLicense = new ListLicense();
		listLicense.setConnectionCount(getMaxLicense(licenseList));
		//add by zhanglu 2014-08-15 bugID:3339 start
		listLicense.setSystemExpire(LICENSE_HOURS);
		//add by zhanglu 2014-08-15 bugID:3339 end
		listLicense.setLicenses(licenseList);
		response.setBody(listLicense);
		return response;
	}

	public NullResponse deleteLicense(LicenseIdParam licenseParam) {
		//TODO锛氬垹闄ゆ巿鏉冩椂锛屽瓨鍦ㄥ浣欑殑杩炴帴鏁版椂濡備綍澶勭悊锛屼笅娆¤繛鎺ユ椂鐢熸晥锛�
		NullResponse response = new NullResponse();
		if (licenseParam == null || licenseParam.getIdlicense() < 0) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		licenseFacade.deleteLicense(licenseParam.getIdlicense());

		connect_count = getMaxLicense(licenseFacade.listLicense(0, null));
		//add by zhanglu 2014-07-05 start
		if (-1 == UkeyMonitorQuartz.isExpired) {
			UkeyMonitorQuartz.isExpired = 0;
		}
		//add by zhanglu 2014-07-05 end
		//add by zhanglu bugId:3342 start
		LicenseMangement.LICENSE_HOURS = 0;
		//add by zhanglu bugId:3342 end
		TaskInfo task = new TaskInfo();
		task.setExecutorClass(MaxConnectCountExecutor.class);
		task.setPara1(String.format("%d", connect_count));
		task.setType(1);
		controller.sendTask(task);
		
		return response;
	}
	
	/**
	 * 鏍规嵁CloudManager鐨勬帴鍙ｄ慨鏀圭殑锛屾煡鐪婥loudManager鐨勭浉鍏虫帴鍙�
	 * @author tanyunhua
	 *
	 */
	@XmlRootElement(name = "idlicense")
	public static class LicenseIdParam implements Serializable {
		private static final long serialVersionUID = 1L;

		private int idlicense;

		public int getIdlicense() {
			return idlicense;
		}

		public void setIdlicense(int idlicense) {
			this.idlicense = idlicense;
		}
	}
	
	public static class ListLicense implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private List<License> licenses;
		
		private int connectionCount;
		
		//add by zhanglu 2014-08-15 bugId:3339 start
		private int systemExpire;
		//add by zhanglu 2014-08-15 bugId:3339 end

		public List<License> getLicenses() {
			return licenses;
		}

		public void setLicenses(List<License> licenses) {
			this.licenses = licenses;
		}

		public int getConnectionCount() {
			return connectionCount;
		}

		public void setConnectionCount(int connectionCount) {
			this.connectionCount = connectionCount;
		}
		//add by zhanglu 2014-08-15 bugId:3339 start
		public int getSystemExpire() {
			return systemExpire;
		}

		public void setSystemExpire(int systemExpire) {
			this.systemExpire = systemExpire;
		}
		//add by zhanglu 2014-08-15 bugId:3339 end
	}

	@XmlRootElement(name = "response")
	public static class ListLicenseResponse extends Response<ListLicense> implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private ListLicense body;
		
		public ListLicense getBody() {
			return body;
		}
		public void setBody(ListLicense body) {
			this.body = body;
		}
	}

	@XmlRootElement(name = "response")
	public static class LicenseResponse extends Response<License> implements Serializable {
		private static final long serialVersionUID = 1L;
		private License body;
		@Override
		public License getBody() {
			return body;
		}
		@Override
		public void setBody(License body) {
			this.body = body;
		}
	}

	@XmlRootElement(name = "fingerprint")
	public static class FingerPrintInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private String content;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}

	@XmlRootElement(name = "response")
	public static class FingerPrintResponse extends Response<FingerPrintInfo> implements Serializable {
		private static final long serialVersionUID = 1L;
		private FingerPrintInfo body;
		@Override
		public FingerPrintInfo getBody() {
			return body;
		}
		@Override
		public void setBody(FingerPrintInfo body) {
			this.body = body;
		}
	}
	
	public FingerPrintResponse downloadFingerPrint()
	{
		FingerPrintResponse response = new FingerPrintResponse();
		//add by zhanglu 2014-07-01 start
		//当前系统不是单机模式时不生成指纹
		if (!appStatusService.isStandalone())
		{
			//返回错误信息不提供集群的指纹
			response.getHead().setError(LICENSE_CLUSTER_TYPE);
			return response;
		}
		//add by zhanglu 2014-07-01 end
		String hwConent = LicenseUtil.getAllHardwareInfo();
		PublicKey pKey = LicenseUtil.getPublicKey();
		if(pKey == null)
		{
			response.getHead().setError(LICENSE_NO_PUBLIC_KEY);
			return response;
		}
		try {
			hwConent = LicenseUtil.encryptByKey(hwConent, pKey);
			hwConent = hwConent.replaceAll("\r\n", "");
		} catch (Exception e) {
			log.info("encrypte error", e);
			response.getHead().setError(LICENSE_ENCRYPT_ERROR);
			return response;
		}
		FingerPrintInfo hwInfo = new FingerPrintInfo();
		hwInfo.setContent(hwConent);
		response.setBody(hwInfo);
		return response;
	}

	public void setLicenseFacade(LicenseFacade licenseFacade) {
		this.licenseFacade = licenseFacade;
		connect_count = getMaxLicense(licenseFacade.listLicense(0, null));
		log.info("setLicenseFacade connect_count is " + connect_count);
	}
	
	//add by zhanglu 2014-07-01 start
	public void setAppStatusService(AppStatusService appStatusService)
	{
		this.appStatusService = appStatusService;
	}
	public void setLicenseServerFacade(LicenseServerFacade licenseServerFacade)
	{
		this.licenseServerFacade = licenseServerFacade;
	}
	//add by zhanglu 2014-07-01 end
}
