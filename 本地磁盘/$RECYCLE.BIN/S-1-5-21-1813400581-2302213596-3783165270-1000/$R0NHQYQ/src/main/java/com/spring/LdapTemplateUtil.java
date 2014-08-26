package com.spring;

import org.springframework.ldap.AuthenticationSource;
import org.springframework.ldap.LdapTemplate;
import org.springframework.ldap.support.LdapContextSource;

public class LdapTemplateUtil {
	
	private static  LdapTemplate template;
	
	public static LdapTemplate getLdapTemplate(final String url,final String base,final String password,final String logDN){
		
		LdapContextSource cs = new LdapContextSource();
	    cs.setCacheEnvironmentProperties(false);
	   //cs.setUrl("ldap://127.0.0.1:389");
	    cs.setUrl(url);
	    //cs.setBase("o=anotherbug,c=com");
	    cs.setBase(base);
	    cs.setAuthenticationSource(new AuthenticationSource() {
	        public String getCredentials() {
		   // return "secret";
	        	return password;
		}
		public String getPrincipal() {
		    //return "cn=manager,o=anotherbug,c=com";
		    return logDN;
		}
	    });
	    template=new LdapTemplate(cs);
	    return template;    
	}	
}
