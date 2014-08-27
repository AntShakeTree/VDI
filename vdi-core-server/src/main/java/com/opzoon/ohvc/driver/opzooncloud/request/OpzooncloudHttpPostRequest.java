/**;
 * 
 */
package com.opzoon.ohvc.driver.opzooncloud.request;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * Opzooncloud POST 方式提交的数据
 * @author maxiaochao 
 * @version V04
 */
public class OpzooncloudHttpPostRequest extends OpzoonCloudRequestManager<HttpPost> {
    private static final Logger log = Logger.getLogger(OpzooncloudHttpPostRequest.class);

    private HttpPost httpPost;

    /**
     * 发送数据
     * 
     * @param jsonObject
     * @return
     * @return
     * @throws Exception 
     */
    public String excuteJSON(JSONObject jsonObject) throws Exception {
	log.info("enter sendMessageByJSONObject.");

	StringEntity entity;
	try {
	    entity = new StringEntity(jsonObject.toString());
	    httpPost.setEntity(entity);
	    return this.execute();
	} catch (IOException e) {
	    log.error(e.getMessage());
	    throw e;
	}
    }



    /**
     * 子类构造
     * 
     * @param url
     */
    private OpzooncloudHttpPostRequest(String ip,String url) throws Exception{
	httpPost = new HttpPost(url);
	super.init(ip, httpPost);
    }

    public static OpzooncloudHttpPostRequest instanceByUrl(String ip,String url) throws Exception{
	return new OpzooncloudHttpPostRequest(ip,url);
    }

}
