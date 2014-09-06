package com.vdi.dao.suport;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

public class TestLdapScript {
    
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws NamingException { 
        
            Hashtable env = new Hashtable();
            
            String userName = "administrator";
            String passWord = "123.com";
            String ldap = "ldaps://20.1.136.193:636";
            
            String keystore = "D:\\Java\\jdk1.7.0\\jre\\lib\\security\\cacerts";
            System.setProperty("javax.net.ssl.trustStroe", keystore);
            
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            
                         
                
            env.put(Context.SECURITY_AUTHENTICATION,"simple"); //No other SALS worked with me    
            env.put(Context.SECURITY_PRINCIPAL,userName); // specify the username ONLY to let Microsoft Happy    
            env.put(Context.SECURITY_CREDENTIALS, passWord);   //the password    
      
            env.put(Context.SECURITY_PROTOCOL, "ssl");
            env.put(Context.PROVIDER_URL,ldap);
            
            DirContext ctx = new InitialLdapContext(env,null); 
           }}
           
            
//        try {    
            
   
//                //Create the search controls        
//                SearchControls searchCtls = new SearchControls();
//            
//                //Specify the attributes to return
//                String returnedAtts[]={"sn","givenName"};
//                searchCtls.setReturningAttributes(returnedAtts);
//            
//                //Specify the search scope
//                searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//     
//                //specify the LDAP search filter
//                //String searchFilter = "(&(ObjectClass=Person)(!(ObjectClass=user)))";
//                //String searchFilter = "(&(objectClass=user)(&(objectClass=Person)(!(userAccountControl=514))))";
//                String searchFilter = "(&(objectClass=user)(mail=*))";
//                       
//                //Specify the Base for the search
//                String searchBase = "DC=xx,DC=net";
//     
//                //initialize counter to total the results
//                int totalResults = 0;
//     
// 
//                // Search for objects using the filter
//                NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);
//     
//                //Loop through the search results
//                while (answer.hasMoreElements()) {
//                    SearchResult sr = (SearchResult)answer.next();
// 
//                totalResults++;
// 
//                System.out.println(">>>" + "Test>>" + sr );
// 
//                // Print out some of the attributes, catch the exception if the attributes have no values
// 
 /*               Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    try {
                    System.out.println("   surname: " + attrs.get("cn").get());
                    System.out.println("   firstname: " + attrs.get("DisplayName").get());
                   
 
                    } 
                    catch (NullPointerException e)  {
                    System.out.println("Errors listing attributes: " + e);
                    }
              }*/
 
 
//            }
// 
//                ctx.close(); 
//        } catch(NamingException e) 
//        {   
//                 System.err.println(e);    
//                     return;
//             }  //if no exception, the user is already authenticated.  
//        System.out.println("OK, successfully authenticating user");
//        }
    
//    }