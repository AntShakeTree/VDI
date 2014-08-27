/**;
 * 
 */
package com.opzoon.ohvc.driver.opzooncloud.request;

import org.apache.http.client.methods.HttpPut;

/**
 * OpzoonCloud PUT 方式提交的数据
 * @author maxiaochao 
 * @version V04
 */
public class HttpPutRequest extends OpzoonCloudRequestManager<HttpPut> {
    private HttpPut httpPut;



    /**
     * 子类构造
     * 
     * @param url
     */
    private HttpPutRequest(String ip,String url) throws Exception{
	httpPut = new HttpPut(url);
	super.init(ip, httpPut);
    }

    public static HttpPutRequest instanceByUrl(String ip,String url) throws Exception{
	return new HttpPutRequest(ip,url);
    }



}
