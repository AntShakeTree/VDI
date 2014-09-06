/**
 * Project Name:vdi-core-server-lls
 * File Name:B.java
 * Package Name:com.vdi.dao.suport
 * Date:2014年8月18日上午8:54:58
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.suport;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.*;
import javax.net.SocketFactory;

public class DummySSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory factory;
   
    public DummySSLSocketFactory() {
   
        try {
       
        SSLContext sslcontext = SSLContext.getInstance("TLS");
       
        sslcontext.init( null, // No KeyManager required
       
        new TrustManager[] { new DummyTrustManager()},
       
        new java.security.SecureRandom());
       
        factory = ( SSLSocketFactory) sslcontext.getSocketFactory();
       
        } catch( Exception ex) { ex.printStackTrace(); }
   
    }
   
    public static SocketFactory getDefault() {
   
    return new DummySSLSocketFactory();
   
    }
   
    public Socket createSocket( Socket socket, String s, int i, boolean flag) throws IOException {
   
    return factory.createSocket( socket, s, i, flag);
   
    }
   
    public Socket createSocket( InetAddress inaddr, int i, InetAddress inaddr1, int j) throws IOException {
   
    return factory.createSocket( inaddr, i, inaddr1, j);
   
    }
   
    public Socket createSocket( InetAddress inaddr, int i) throws IOException {
   
    return factory.createSocket( inaddr, i);
   
    }
   
    public Socket createSocket( String s, int i, InetAddress inaddr, int j) throws IOException {
   
    return factory.createSocket( s, i, inaddr, j);
   
    }
   
    public Socket createSocket( String s, int i) throws IOException {
   
    return factory.createSocket( s, i);
   
    }
   
    public String[] getDefaultCipherSuites() {
   
    return factory.getSupportedCipherSuites();
   
    }
   
    public String[] getSupportedCipherSuites() {
   
    return factory.getSupportedCipherSuites();
   
    }

}