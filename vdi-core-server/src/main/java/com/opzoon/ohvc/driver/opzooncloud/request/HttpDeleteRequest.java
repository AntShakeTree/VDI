/**;
 * 
 */
package com.opzoon.ohvc.driver.opzooncloud.request;

import org.apache.http.client.methods.HttpDelete;
import org.apache.log4j.Logger;

/**
 * OpzoonCLOUD DELETE 方式提交的数据
 * @author maxiaochao 
 *  @version V04
 */
public class HttpDeleteRequest extends OpzoonCloudRequestManager<HttpDelete> {
	private HttpDelete httpDelete;
	private static final Logger log = Logger.getLogger(HttpDeleteRequest.class);

	
	/**
	 * 子类构造
	 * 
	 * @param url
	 */
	private HttpDeleteRequest(String ip,String url) throws Exception{
		httpDelete = new HttpDelete(url);
		super.init(ip, httpDelete);
	}

	public static HttpDeleteRequest instanceByUrl(String ip, String url) throws Exception{
	    log.info(" :: HttpDeleteRequest enter.");
		return new HttpDeleteRequest(ip,url);
	}

}
