/**
 * 
 */
package com.opzoon.ohvc.request;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * request 抽象类
 * 
 * @author maxiaochao
 * @version 1.1
 */
public class Request {
	private static volatile PoolingClientConnectionManager cm = null;
	private static final int SOCKET_OPERATION_TIMEOUT = 10 * 60 * 1000;

	/**
	 * 
	 * @param ip
	 * @return
	 * @return HttpClient
	 * @author：maxiaochao 2012-9-17 上午11:31:01
	 */
	public static HttpClient getClient(String baseUrl) {
		HttpClient client = null;

		try {
			client = new DefaultHttpClient(getPool(baseUrl), getParams());
		} catch (Exception e) {
			client = new DefaultHttpClient();
			getParams(client);
		}

		return client;

	}

	/**
	 * Request.shutDown()
	 * 
	 * @param response
	 * 
	 * @return void
	 * @author：maxiaochao 2012-9-17 上午11:35:06
	 */
	public static void shutDown(HttpResponse response, HttpClient client) {

		try {
			if (response != null) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {

					entity.getContent().close();
				}
			}
		} catch (Exception e) {
		}
		try {
			if (client != null) {
				ClientConnectionManager clientConnectionManager = client
						.getConnectionManager();
				clientConnectionManager.closeExpiredConnections();
				clientConnectionManager.closeIdleConnections(10 * 1000,
						TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
		}
	}

	// }

	/**
	 * 
	 * @return: PoolingClientConnectionManager
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	private static PoolingClientConnectionManager getPool(String ip)
			throws Exception {
		try {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			// javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
			// 避免HttpClient的”SSLPeerUnverifiedException: peer not
			// authenticated”异常
			// 不用导入SSL证书
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
				}
			};

			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", 443, ssf));
			// javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
			if (cm == null) {
				cm = new PoolingClientConnectionManager(schemeRegistry);
				cm.setMaxTotal(400);
				cm.setDefaultMaxPerRoute(150);
				cm.setDefaultMaxPerRoute(100);
			}
			HttpHost route = new HttpHost(ip, 80);
			cm.setMaxPerRoute(new HttpRoute(route), 50);

		} catch (Exception e) {
			throw e;
		}
		return cm;
	}

	/**
	 * 
	 * @return: HttpParams
	 * @return
	 * @author david..maxc
	 */
	private static HttpParams getParams() {// ----------------------------------------------------------------------
											// //
		HttpParams params = new BasicHttpParams();
		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params,
				SOCKET_OPERATION_TIMEOUT / 2);
		HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		// Don't handle redirects -- return them to the caller. Our code
		// often wants to re-POST after a redirect, which we must do ourselves.
		// HttpClientParams.setRedirecting(params, false);
		return params;
	}

	/**
	 * The HttpConnectionParams need to be passed to a connection manager
	 * 
	 * @return: void
	 * @param httpclient
	 */
	private static void getParams(HttpClient httpclient) {
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT,
				SOCKET_OPERATION_TIMEOUT / 2);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				SOCKET_OPERATION_TIMEOUT);
	}
}
