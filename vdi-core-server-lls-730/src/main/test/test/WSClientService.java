/**   
 * Title: CrawlerService.java 
 * @Package test.crawler 
 * : TODO(用一句话描述该文件做什么) 
 * @author david   
 * @date 2013-1-4 下午4:10:26 
 * @version V1.0.0.0   
 */
package test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import sun.misc.BASE64Encoder;

import com.vdi.common.ConfigUtil;

/**
 * ClassName: CrawlerService
 * : TODO
 * @date : 2013-1-4 下午4:10:26
 * @author : david
 * @version:1.0.0.0
 */

public class WSClientService {



	public static void main(String[] args) throws IOException {
		WSClientService clientService = new WSClientService();
		
		ConfigUtil.loadConfigFileByPath("/test.properties");
//		testWS(ConfigUtil.getCustomKey("funcation"), "{}");
//		testGetWS();
		clientService.testRole(ConfigUtil.getCustomKey("funcation"),ConfigUtil.getCustomKey("ticket"),ConfigUtil.getCustomKey("req"));
//		clientService.login(ConfigUtil.getCustomKey("login.url"));
	}

	public static void addHeader(HttpRequestBase method, Map<String, String> paramterStr) {
		for (String key : paramterStr.keySet()) {
			System.out.println(paramterStr.get(key) + "");
			method.addHeader(key, paramterStr.get(key) + "");
		}
	}

	public static void testWS(String service, String data) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		ConfigUtil.loadConfigFileByPath("/test.properties");
		String url=ConfigUtil.getCustomKey("url");
		url=url+ service;
		System.out.println(url);
		HttpPost httpPost = new HttpPost(url);
		Map<String, String> paramterStr = new HashMap<String, String>();
//		paramterStr.put("Content-Type", "applicationopzoon-v4+json");
		paramterStr.put("Content-Type", "application/json");
		if (!data.equals("")) {
			HttpEntity entity = new StringEntity(data,Charset.forName("UTF-8"));
			httpPost.setEntity(entity);
		}
		System.out.println(data);
		addHeader(httpPost, paramterStr);
		HttpResponse res = httpClient.execute(httpPost);
		HttpEntity entity2 = res.getEntity();
		System.out.println(EntityUtils.toString(entity2));
		httpPost.abort();
		httpClient.getConnectionManager().shutdown();
	}
	public static void testGetWS() throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		ConfigUtil.loadConfigFileByPath("/test.properties");
		String url=ConfigUtil.getCustomKey("url");
		System.out.println(url);
		HttpPost get = new HttpPost(url);
		Map<String, String> paramterStr = new HashMap<String, String>();
//		paramterStr.put("Content-Type", "applicationopzoon-v4+json");
		//paramterStr.put("Content-Type", "application/json;charset=utf-8");
		

		HttpResponse res = httpClient.execute(get);
		HttpEntity entity2 = res.getEntity();
		System.out.println(EntityUtils.toString(entity2));
		get.abort();
		httpClient.getConnectionManager().shutdown();
	}

	public void login(String url) throws IOException {
		ConfigUtil.loadConfigFileByPath("/test.properties");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		// HttpGet get = new HttpGet(url);
		try {
			Map<String, String> paramterStr = new HashMap<String, String>();
			paramterStr.put("Content-Type", "application/json");
			addHeader(post, paramterStr);
			System.out.println(ConfigUtil.getCustomKey("login.req"));
			String param=ConfigUtil.getCustomKey("login.req");
			post.setEntity(new StringEntity(param,Charset.forName("UTF-8")));
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			System.out.println(EntityUtils.toString(entity));
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

	public void testRole(String string, String ticket,String data) throws IOException {
		ConfigUtil.loadConfigFileByPath("/test.properties");
		HttpClient httpClient = new DefaultHttpClient();
		 HttpPost post = new HttpPost(ConfigUtil.getCustomKey("url")+string);
		try {
			String token = "Basic " + (new BASE64Encoder()).encode( (ticket + ":").getBytes() );
			Map<String, String> paramterStr = new HashMap<String, String>();
			paramterStr.put("Content-Type", "application/json");
			paramterStr.put("Authorization", token);
			addHeader(post, paramterStr);
			post.setEntity(new StringEntity(data));
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			System.out.println(EntityUtils.toString(entity));
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

//	public void reg() throws IOException {
//		ConfigUtil.load("/test.properties");
//		HttpClient httpClient = new DefaultHttpClient();
//		String fun = ConfigUtil.getBykey("reg");
//		HttpPost post = new HttpPost(url+fun);
//		// HttpGet get = new HttpGet(url);
//		try {
//			UserReq user = new UserReq();
//			user.setUsername("admin111");
//			user.setPassword("admin");
//			Map<String, String> paramterStr = new HashMap<String, String>();
//			paramterStr.put("Content-Type", "application/json;charset=utf-8");
//			addHeader(post, paramterStr);
//			post.setEntity(new StringEntity(ParseJSON.toJson(user)));
//
//			HttpResponse response = httpClient.execute(post);
//			HttpEntity entity = response.getEntity();
//			System.out.println(EntityUtils.toString(entity));
//		} catch (IOException e) {
//			// e.printStackTrace();
//			throw e;
//		} finally {
//			httpClient.getConnectionManager().shutdown();
//		}
//
//	}

//	public void publish(Object obj) throws IOException {
//		ConfigUtil.loadConfigFileByPath("/test.properties");
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpPost post = new HttpPost(url);
//		// HttpGet get = new HttpGet(url);
//		try {
//
//			Map<String, String> paramterStr = new HashMap<String, String>();
//			paramterStr.put("Content-Type", "application/json; charset=UTF-8");
//			addHeader(post, paramterStr);
//	
//
//			post.setEntity(new StringEntity(ParseJSON.toJson(obj)));
//
//			HttpResponse response = httpClient.execute(post);
//			HttpEntity entity = response.getEntity();
//			System.out.println(EntityUtils.toString(entity));
//		} catch (IOException e) {
//			// e.printStackTrace();
//			throw e;
//		} finally {
//			httpClient.getConnectionManager().shutdown();
//		}
//
//	}

	@Test
	public void testGetU() throws ClientProtocolException, IOException{
			ConfigUtil.loadConfigFileByPath("/test.properties");
			testWS(ConfigUtil.getCustomKey("funcation"), "{}");
	}

}
