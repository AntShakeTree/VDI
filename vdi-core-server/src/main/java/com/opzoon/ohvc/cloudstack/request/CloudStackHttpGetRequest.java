/**
 * 
 */
package com.opzoon.ohvc.cloudstack.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.opzoon.ohvc.cloudstack.domain.CloudStackUser;
import com.opzoon.ohvc.common.GenerateURL;
import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.session.Session;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * cloudstack get方式提交
 * 
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class CloudStackHttpGetRequest extends
		CloudStackHttpRequestManager<HttpGet> {
	static Logger log = Logger.getLogger(CloudStackHttpGetRequest.class);
	@SuppressWarnings({ "rawtypes" })
	public CloudStackHttpGetRequest(String ip, BaseDomain baseDomain)
			throws Exception {
		if(ip == null || ip.equals(""))
		{
			throw new Exception("ip is empty");
		}
		String apiUrl = baseDomain.toString();//
		this.apiUrl = apiUrl;
		injectRequest(ip);

	}

	/*
	 * @see com.opzoon.client.cloudstack.request.CloudStackHttpRequestManager#
	 * injectRequest()
	 */
	@Override
	public void injectRequest(String ip) throws Exception {
		Certificate certificate = (Certificate) Session.getCertificate(ip);
		CloudStackUser u=	(CloudStackUser) certificate.getUser();
		String sessionkey = u.getSessionkey();
		log.info("cloudstack--------------------"+u.getSecretKey());
		String finelurl = GenerateURL.getFinalUrl(ip, this.apiUrl,sessionkey);
		log.info("cloudstack--------------------"+finelurl+"---------------------");
		this.request = new HttpGet(finelurl);
		init(ip);
	}
}
