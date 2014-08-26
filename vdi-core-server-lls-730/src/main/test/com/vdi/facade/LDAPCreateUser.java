//package com.vdi.facade;
//import java.io.UnsupportedEncodingException;
//import java.util.Hashtable;
//
//
//import javax.naming.*;
//
//import javax.naming.ldap.LdapContext;
//
//import javax.naming.ldap.InitialLdapContext;
//
//import javax.naming.directory.*;
//
//import java.util.Hashtable;
//
//import java.util.Enumeration;
//
//import java.io.IOException;
//
//import java.io.UnsupportedEncodingException;
//
//
///**
//* Sample JNDI client ADD application to demonstrate how to create a new user entry in MS-ADAM with user account never expires
//*/
//public class LDAPCreateUser {
//public static void main(String[] args) throws UnsupportedEncodingException
//{
//Hashtable env = new Hashtable();
//
//env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
//
//env.put(Context.PROVIDER_URL, "ldap://esnth001:636/dc=ldapbasedn,dc=com");
//
//env.put(Context.SECURITY_AUTHENTICATION, "simple");
//
//env.put(Context.SECURITY_PROTOCOL, "ssl");
//
//env.put(Context.SECURITY_PRINCIPAL, "cn=adminuser,dc=ldapbasedn,dc=com");
//
//env.put(Context.SECURITY_CREDENTIALS, "adminpwd");
//
//
//try {
//
//// Create the initial context
//// DirContext ctx = new InitialDirContext(env);
//
//LdapContext ctx = new InitialLdapContext(env,null);
//
//// The distinguished name of the new entry
//String dn = "cn="+args[0]+",OU=People,OU=Vap73Nagesh";
//
//
//// Create attributes to be associated with the new entry
//Attributes attrs = new BasicAttributes(true);
//
//
//// Objectclass -- required in MUST list
//Attribute oc = new BasicAttribute("objectclass");
//// required by 'top'
//oc.add("top");
//
//oc.add("person");
//
//oc.add("organizationalPerson");
//
//oc.add("user");
//
//attrs.put(oc);
//
//System.out.println(args[0]);
//
//// Other mandatory attributes -- required in MUST list
//attrs.put("uid", args[0]);
//
//attrs.put("sn", "SomeSN");
//// required by 'person'
//attrs.put("givenName","SomeGN");
//
//attrs.put("cn", args[0]);
//// required by 'person'
//
//// Optional attributes -- but they must be defined in schema
//
//attrs.put("mail","usermail@mydomain.com");
//
//
////this 2 props are needed for user creation without password expiration in MS-ADAM.
//
//attrs.put("msDS-UserAccountDisabled", "FALSE");
//
//attrs.put("msDS-UserDontExpirePassword", "TRUE");
//
//
//attrs.put("ou", "people");
//
//// Create the context
//Context result = ctx.createSubcontext(dn, attrs);
//
//System.out.println("Created account for: " + dn);
//
//
////set password is a ldap modfy operation
////and we'll update the userAccountControl
////enabling the acount and force the user to update ther password
////the first time they login
//ModificationItem[] mods = new ModificationItem[1];
//
//
////Replace the "unicdodePwd" attribute with a new value
////Password must be both Unicode and a quoted string
//String newQuotedPassword = """+args[0]+""";
//
//byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
//
//
//
//mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", newUnicodePassword));
//
//// Perform the update
//ctx.modifyAttributes(dn, mods);
//
//System.out.println("Set password & updated userccountControl");
//
//
//// Close the contexts when we're done
//result.close();
//
//ctx.close();
//
//
//}catch(NamingException e){
//e.printStackTrace();
//
//}
//
//}
//
//};
//
//byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
//
//
//
//mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", newUnicodePassword));
//
//// Perform the update
//ctx.modifyAttributes(dn, mods);
//
//System.out.println("Set password & updated userccountControl");
//
//
//// Close the contexts when we're done
//result.close();
//
//ctx.close();
//
//
//}catch(NamingException e){
//e.printStackTrace();
//
//}
//
//}
//
//}