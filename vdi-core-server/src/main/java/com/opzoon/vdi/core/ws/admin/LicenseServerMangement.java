package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.UKEY_GUID_ERR;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opzoon.vdi.core.controller.Controller;
import com.opzoon.vdi.core.domain.LicenseServer;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.LicenseServerFacade;
import com.opzoon.vdi.core.util.LicenseUtil;
import com.opzoon.vdi.core.ws.Services.Response;
/**
 * 
 * @author zhanglu
 *
 */
public class LicenseServerMangement {
	//TODO锛氶粯璁�0涓苟鍙戣繛鎺ユ巿鏉冨浣曞疄鐜般�
	public static final int INIT_CONNECT_COUNT = 10;
	public static int connect_count = INIT_CONNECT_COUNT;
	private LicenseServerFacade licenseServerFacade;
	private static final Logger log = LoggerFactory.getLogger(LicenseServerMangement.class);

	@Autowired
	private Controller controller;
	
	public ListLicenseServerResponse ListLicenseServer(LicenseServerIdParam licenseServerIdParam){
		ListLicenseServerResponse response = new ListLicenseServerResponse();
		if(licenseServerIdParam == null)
			licenseServerIdParam = new LicenseServerIdParam();

		List<LicenseServer> licenseServerList = licenseServerFacade.listLicenseServer(licenseServerIdParam.getIdlicenseserver(), null);
		ListLicenseServer list = new ListLicenseServer();
		list.setLicenseServers(licenseServerList);
		response.setBody(list);
		return response;
	}
	
	public LicenseServerResponse createLicenseServer(LicenseServer licenseServer) {
		LicenseServerResponse response = new LicenseServerResponse();
		int error = 0;
		try {
			error = licenseServerFacade.createLicenseServer(licenseServer);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		response.setBody(licenseServer);
		return response;
	}
	
	public LicenseServerResponse updateLicenseServer(LicenseServer licenseServer) {
		LicenseServerResponse response = new LicenseServerResponse();
		int error = 0;
		try {
			error = licenseServerFacade.updateLicenseServer(licenseServer);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		response.setBody(licenseServer);
		return response;
	}
	
	/**
	 * 鏍规嵁CloudManager鐨勬帴鍙ｄ慨鏀圭殑锛屾煡鐪婥loudManager鐨勭浉鍏虫帴鍙�
	 * @author tanyunhua
	 *
	 */
	@XmlRootElement(name = "idlicenseserver")
	public static class LicenseServerIdParam implements Serializable {
		private static final long serialVersionUID = 1L;

		private int idlicenseserver;

		public int getIdlicenseserver() {
			return idlicenseserver;
		}

		public void setIdlicenseserver(int idlicenseserver) {
			this.idlicenseserver = idlicenseserver;
		}
	}
	
	public static class ListLicenseServer implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private List<LicenseServer> licenseServer;
		
		public List<LicenseServer> getLicenseServers() {
			return licenseServer;
		}

		public void setLicenseServers(List<LicenseServer> licenseServer) {
			this.licenseServer = licenseServer;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ListLicenseServerResponse extends Response<ListLicenseServer> implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private ListLicenseServer body;
		
		public ListLicenseServer getBody() {
			return body;
		}
		public void setBody(ListLicenseServer body) {
			this.body = body;
		}
	}

	@XmlRootElement(name = "response")
	public static class LicenseServerResponse extends Response<LicenseServer> implements Serializable {
		private static final long serialVersionUID = 1L;
		private LicenseServer body;
		@Override
		public LicenseServer getBody() {
			return body;
		}
		@Override
		public void setBody(LicenseServer body) {
			this.body = body;
		}
	}

	@XmlRootElement(name = "guid")
	public static class GuidInfo implements Serializable {
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
	public static class GuidResponse extends Response<GuidInfo> implements Serializable {
		private static final long serialVersionUID = 1L;
		private GuidInfo body;
		@Override
		public GuidInfo getBody() {
			return body;
		}
		@Override
		public void setBody(GuidInfo body) {
			this.body = body;
		}
	}
	
	public GuidResponse downloadGuid()
	{
		GuidResponse response = new GuidResponse();
		//获取Guid
		String guid = LicenseUtil.getGuid();
		System.out.println(guid);
		if (null == guid || "".equals(guid))
		{
			//返回错误信息不提供集群的指纹
			response.getHead().setError(UKEY_GUID_ERR);
			return response;
		}
		GuidInfo hwInfo = new GuidInfo();
		hwInfo.setContent(guid);
		response.setBody(hwInfo);
		return response;
	}
	public void setLicenseServerFacade(LicenseServerFacade licenseServerFacade) {
		this.licenseServerFacade = licenseServerFacade;
	}
}
