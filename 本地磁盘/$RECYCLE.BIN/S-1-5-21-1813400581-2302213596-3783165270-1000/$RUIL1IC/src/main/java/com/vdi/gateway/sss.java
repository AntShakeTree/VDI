package com.vdi.gateway;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class sss {
	public static LdapContext  getConnectionFormPool() throws NamingException {
		
		//加载证书
		String keystore = "D:\\Java\\jre7\\lib\\security\\cacerts";
		System.setProperty("javax.net.ssl.trustStore", keystore);
		
		//JNDI 连接属性设置
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldaps://20.1.136.193:636");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,
				"cn=Administrator,cn=Users,dc=domain1,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "234.com");
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		return  new InitialLdapContext(env, null);
	}
	public static void main(String[] args) throws NamingException {
//		sss.getConnectionFormPool(); 
		
	}
}
