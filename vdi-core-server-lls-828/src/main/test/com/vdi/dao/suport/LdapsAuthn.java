/**
 * Project Name:vdi-core-server-lls
 * File Name:C.java
 * Package Name:com.vdi.dao.suport
 * Date:2014年8月18日上午10:12:44
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.suport;
import java.util.Collections;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapsAuthn {
    public static void main(String args[]) {
        String server = "20.1.136.193";
        String port = "636";
        String admin = "administrator\\domain1";
        String adminPass = "123.com";
        String testUser = "abc";
        String baseDN = "dc=domain1,dc=com";
        if (connect(server, port, admin, adminPass, testUser, baseDN)) {
            System.out.println("Successful");
        } else {
            System.out.println("Fail");
        }
    }
   
    public static boolean connect(String server, String port, String user, String passwd, String testUser, String baseDN) {
        boolean result = false;
        Properties env = new Properties();
        String ldapURL = "ldaps://" + server + ":" + port;
//       Collections.synchronizedSet(s)
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");  
        env.put(Context.SECURITY_AUTHENTICATION, "simple");  
        env.put(Context.SECURITY_PRINCIPAL, user);  
        env.put(Context.SECURITY_CREDENTIALS, passwd);  
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.REFERRAL,"ignore");
        env.put(Context.SECURITY_PROTOCOL,"ssl");
       
        env.put("java.naming.ldap.factory.socket", "com.vdi.dao.suport.DummySSLSocketFactory");
        try {
            DirContext ctx = new InitialDirContext(env);
//            SearchControls searchCtls = new SearchControls();  
//            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);  
//            NamingEnumeration<?> results = ctx.search(baseDN, "uid=" + testUser, searchCtls);  
//            while (results.hasMoreElements()) {  
//                SearchResult sr = (SearchResult) results.next();     
//                Attributes attributes = sr.getAttributes();
//                System.out.println(attributes);
//            }  
//            ctx.close();  
//            result = true;
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return result;
    }
}

 