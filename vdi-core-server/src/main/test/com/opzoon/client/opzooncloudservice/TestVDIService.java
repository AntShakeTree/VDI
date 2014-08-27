package com.opzoon.client.opzooncloudservice;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestVDIService {
	public static void main(String[] args) {
		HttpPost httppost = new HttpPost(
				"http://192.168.100.63:8080/vdicore/services/destroyDesktop");

		httppost.setHeader("Content-Type",
				"application/opzoon-v4+json; charset=UTF-8");
		httppost.setEntity(new StringEntity("{\"desktopid\":131}", Charset
				.forName("UTF-8")));
		httppost.setHeader(
				"Authorization",
				"Basic "
						+ "7DIYehoduZbBKpTLdpIEJJtPNtwAbQB9f6O8ysVGPiz4btS0AYvCxO7erhCcNPRH");
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(httppost);
			// System.out.println(response.getEntity().toString());
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// httppost.
			// client.execute(arg0)
			// res
			client.getConnectionManager().shutdown();
		}
	}
	String login(){
		HttpPost httppost = new HttpPost(
				"http://192.168.100.63:8080/vdicore/services/loginSession");

		httppost.setHeader("Content-Type",
				"application/opzoon-v4+json; charset=UTF-8");
		httppost.setEntity(new StringEntity("{\"admin\":\"\"}", Charset
				.forName("UTF-8")));
		httppost.setHeader(
				"Authorization",
				"Basic "
						+ "7DIYehoduZbBKpTLdpIEJJtPNtwAbQB9f6O8ysVGPiz4btS0AYvCxO7erhCcNPRH");
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(httppost);
			// System.out.println(response.getEntity().toString());
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// httppost.
			// client.execute(arg0)
			// res
			client.getConnectionManager().shutdown();
		}
	}
}
