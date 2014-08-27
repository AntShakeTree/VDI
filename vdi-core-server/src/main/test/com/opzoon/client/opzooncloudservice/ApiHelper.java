package com.opzoon.client.opzooncloudservice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.Date;
import java.util.List;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.opzoon.vdi.vo.BaseVo;

import sun.misc.BASE64Encoder;

public class ApiHelper {
	
	private String authString;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		System.out.println((new sun.misc.BASE64Encoder()).encode( "abc".getBytes() ));
//		boolean islogin = true;
//		if(islogin){
//			LoginVo lvo = new LoginVo();
//			lvo.setUsername("testcli");
//			lvo.setPassword("111111");
//			lvo.setLogintype(1);
//			try {
//				JSONObject res = doRequestApiWithoutSession("loginSession", JSONHelper.vo2JSON(lvo).toString());
//				System.out.println(res);
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//		}
//		if(!islogin){
////		UserVo uVo = new UserVo();
////		uVo.setAddress("abc");
////		uVo.setIdcard("123");
////		uVo.setIdcardtype("0");
////		uVo.setNotes("beizhu1");
////		uVo.setPassword("111111");
////		uVo.setRealname("lixin");
////		uVo.setTelephone("138110999xx");
////		uVo.setUsername("lixin");
////		uVo.setUsertype(1);
////		uVo.setPagesize(10);
////		JSONObject j;
////		System.out.println(j = JSONHelper.vo2JSON(uVo));
////		JSONHelper.jsonFilter(j, ApiSelector.LIST_USER.getValidAttrs());
//		ApiHelper a = new ApiHelper("zs5pwwwzval7CRwnArvvNjLNdIoaNe8J0ffb8FWspbeOgWbxftfDt90H03GUPVt4");
//		try {
//			JSONObject res = a.doRequestApi("listResources", "{\"resourcetype\": 1, \"userid\": -1}", "all");
//			System.out.println(res);
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		}
		//M1FUZW5KanRiY3k1VVQ3eGFidGVZMWVNdDI1ZVVrNXpZZ0J6THQ1TUZYZU05Nm5xUjU3NWJMTDJSUzJMTEtQSDo=
	}
	
	private static final Log logger = LogFactory.getLog(ApiHelper.class);
	
	private String ticket;
	
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public ApiHelper(String ticket){
		this.ticket = ticket;
		this.authString = "Basic " + new String(new Base64().encode((ticket + ":").getBytes()));
		//System.out.println(this.authString);
		//this.authString = "Basic QlFHTXJHRzk1M1pUTHpvOE1lZGdhS0pWQVBSV0x4RWkzUWxTQTVsNXp0SHVvRkZadVZSQlRkeXlQbDJxR1pLZDo=";
		//this.authString = "Basic " + (new BASE64Encoder()).encode( (ticket + ":").getBytes() ).replace("\n", "");
	}
	
	/**
	 * 调用vdi_core的api，将等到的结果返回
	 * @param apiName api名称
	 * @param jsonInput
	 * @return 返回的结果为json对象
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static JSONObject doRequestApiWithoutSession(String apiName, String jsonInput) throws Exception{
		HttpClient httpclient = new DefaultHttpClient();
		//处理https
        try {
        	if(GlobalKey.USE_HTTPS){
	            KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
	            InputStream instream = ApiHelper.class.getResourceAsStream("/cacerts");
	            try {
	                trustStore.load(instream, "123abc".toCharArray());
	            } finally {
	                try { instream.close(); } catch (Exception ignore) {}
	            }
	
	            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
	            //不校验域名
	            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	            Scheme sch = new Scheme("https", Integer.parseInt(GlobalKey.SERVER_PORT), socketFactory);
	            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        	}
            HttpPost httppost = new HttpPost(GlobalKey.PROTOHEAD + GlobalKey.SERVER_IP + ":" + GlobalKey.SERVER_PORT + "/vdicore/services/" + apiName);

            httppost.setHeader("Content-Type", "application/opzoon-v4+json; charset=UTF-8");
            httppost.setEntity(new StringEntity(jsonInput, Charset.forName("UTF-8")));
            
            logger.info("executing request" + httppost.getRequestLine());
            logger.debug("--->Body: " + jsonInput);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            res.put("statusCode", response.getStatusLine().getStatusCode());
            String body = EntityUtils.toString(entity);
            try{
            	res.put("body", JSONObject.fromObject(body));
            }catch(Exception e){
            	res.put("body", body);
            }
            JSONObject hj = new JSONObject();
            Header[] hs = response.getAllHeaders();
            for(Header h : hs){
            	hj.put(h.getName(), h.getValue());
            }
            res.put("header", hj);
            if(!"".equals(jsonInput)){
            	res.put("rc", JSONObject.fromObject(jsonInput));
            }
            
            
            
        }catch(Exception e){e.printStackTrace();logger.error(e);} finally {
            httpclient.getConnectionManager().shutdown();
        }
        logger.debug(res.toString());
		return res;
	}
	
	/**
	 * 此方法相对于withoutSession方法多了一个ticket属性，且写成authString
	 * @param req
	 * @param as api选择器
	 * @return
	 * @throws Exception
	 */
	public JSONObject doRequestApi(HttpServletRequest req, ApiSelector as) throws Exception{
		BaseVo vo = JSONHelper.populateVo(as, req);
		JSONObject j = JSONHelper.vo2JSON(vo);
		if(as.getValidAttrs() != null){
			JSONHelper.jsonFilter(j, as.getValidAttrs());
		}
		return doRequestApi(as.getApiName(), j.toString(), "all");
	}
	
	public JSONObject doRequestApiNoWait(HttpServletRequest req, ApiSelector as) throws Exception{
		BaseVo vo = JSONHelper.populateVo(as, req);
		JSONObject j = JSONHelper.vo2JSON(vo);
		if(as.getValidAttrs() != null){
			JSONHelper.jsonFilter(j, as.getValidAttrs());
		}
		return doRequestApiNoWait(as.getApiName(), j.toString(), "all");
	}
	
	public JSONArray doRequestApiMulti(HttpServletRequest req, ApiSelector as, String multiAttr) throws Exception{
		JSONArray arr = new JSONArray();
		List<BaseVo> vos = JSONHelper.populateVoMulti(as, req, multiAttr);
		for(BaseVo vo : vos){
			JSONObject j = JSONHelper.vo2JSON(vo);
			if(as.getValidAttrs() != null){
				JSONHelper.jsonFilter(j, as.getValidAttrs());
			}
			JSONObject res = doRequestApi(as.getApiName(), j.toString(), "all");
			JSONHelper.processApiStatus(res);
			arr.add(res);
		}
		return arr;
	}
	
	public JSONObject doRequestApi(String cont, ApiSelector as) throws Exception{
		
		return doRequestApi(as.getApiName(), cont, "all");
	}
	
	@SuppressWarnings("deprecation")
	public JSONObject doRequestApi(String apiName, String jsonInput, String responsePart) throws Exception{
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject res = new JSONObject();
        try {
        	if(GlobalKey.USE_HTTPS){
	            KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
	            InputStream instream = ApiHelper.class.getResourceAsStream("/cacerts");
	            try {
	                trustStore.load(instream, "123abc".toCharArray());
	            } finally {
	                try { instream.close(); } catch (Exception ignore) {}
	            }
	
	            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
	            //不校验域名
	            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	            Scheme sch = new Scheme("https", Integer.parseInt(GlobalKey.SERVER_PORT), socketFactory);
	            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        	}
            HttpPost httppost = new HttpPost(GlobalKey.PROTOHEAD + GlobalKey.SERVER_IP + ":" + GlobalKey.SERVER_PORT + "/vdicore/services/" + apiName);

            httppost.addHeader("Authorization", authString);
            httppost.setHeader("Content-Type", "application/opzoon-v4+json");
            httppost.setEntity(new StringEntity(jsonInput, Charset.forName("UTF-8")));
            
            logger.info("executing request" + httppost.getRequestLine());
            logger.debug("--->Body: " + jsonInput);
            long startApiTime = new Date().getTime();
            
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if("all".equals(responsePart) || "statusCode".equals(responsePart)){
            	res.put("statusCode", response.getStatusLine().getStatusCode());
            }
            if("all".equals(responsePart) || "body".equals(responsePart)){
	            String body = EntityUtils.toString(entity);
	            
	            try{
	            	res.put("body", JSONObject.fromObject(body));
	            }catch(Exception e){
	            	res.put("body", body);
	            }
            }
            if("all".equals(responsePart) || "header".equals(responsePart)){
	            JSONObject hj = new JSONObject();
	            for(Header h : response.getAllHeaders()){
	            	hj.put(h.getName(), h.getValue());
	            }
	            res.put("header", hj);
            }
            if("all".equals(responsePart)){
            	if(!"".equals(jsonInput)){
                	res.put("rc", JSONObject.fromObject(jsonInput));
                }
            	//res.put("rc", JSONObject.fromObject(jsonInput));
            	res.put("apiTime", new Date().getTime() - startApiTime);
            	res.put("apiName", apiName);
            }
            
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        logger.info("RESP-->" + res.toString());
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public JSONObject doRequestApiNoWait(String apiName, String jsonInput, String responsePart) throws Exception{
		final HttpClient httpclient = new DefaultHttpClient();
		JSONObject res = new JSONObject();
        try {
        	if(GlobalKey.USE_HTTPS){
	            KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
	            InputStream instream = ApiHelper.class.getResourceAsStream("/cacerts");
	            try {
	                trustStore.load(instream, "123abc".toCharArray());
	            } finally {
	                try { instream.close(); } catch (Exception ignore) {}
	            }
	
	            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
	            //不校验域名
	            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	            Scheme sch = new Scheme("https", Integer.parseInt(GlobalKey.SERVER_PORT), socketFactory);
	            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        	}
            final HttpPost httppost = new HttpPost(GlobalKey.PROTOHEAD + GlobalKey.SERVER_IP + ":" + GlobalKey.SERVER_PORT + "/vdicore/services/" + apiName);

            httppost.addHeader("Authorization", authString);
            httppost.setHeader("Content-Type", "application/opzoon-v4+json");
            httppost.setEntity(new StringEntity(jsonInput, Charset.forName("UTF-8")));
            
            logger.info("executing request" + httppost.getRequestLine());
            logger.debug("--->Body: " + jsonInput);
            
            Thread hth = new Thread() {
                @Override
                public void run() {
                    try {
                        httpclient.execute(httppost);
                    } catch (IllegalArgumentException ec) {
                        interrupted();
                    } catch (ClientProtocolException e) {
                        interrupted();
                    } catch (IOException e) {
                        interrupted();
                    } finally {
                    	httpclient.getConnectionManager().shutdown();
                    }
                }
           };
           hth.start();
           
            if("all".equals(responsePart) || "statusCode".equals(responsePart)){
            	res.put("statusCode", 200);
            }
            if("all".equals(responsePart) || "body".equals(responsePart)){
	            res.put("body", JSONObject.fromObject("{\"head\":{\"error\":0}}"));
            }
            if("all".equals(responsePart) || "header".equals(responsePart)){
	            res.put("header", JSONObject.fromObject("{}"));
            }
            if("all".equals(responsePart)){
            	res.put("rc", JSONObject.fromObject(jsonInput));
            	res.put("apiName", apiName);
            }
            
        } finally {
            //httpclient.getConnectionManager().shutdown();
        }
        logger.info("RESP-->" + res.toString());
		return res;
	}
	
    
    
}
