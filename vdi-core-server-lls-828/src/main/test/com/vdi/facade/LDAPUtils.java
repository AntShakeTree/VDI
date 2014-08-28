package com.vdi.facade;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.vdi.common.RuntimeUtils;

public abstract class LDAPUtils {
	
	private LDAPUtils () {}
	
	public static void main(String[] args) throws Exception {
//		connect("ldap://192.168.100.100:389", "dc=opzoondev,dc=com", "opzoondev\\morigen", "123@qwe");
		DirContext ctx=	createDirContext("20.1.136.193",389,  "domain1\\administrator", "123.com");
		connect(ctx,"dc=domain1,dc=com");
	}

	public static DirContext createDirContext(
			String domainservername,
			int port,
			String binddn,
			String bindpass) throws NamingException {
	
		Hashtable<String, String> environment = new Hashtable<String, String>();
//		environment.put(LdapContext.CONTROL_FACTORIES, "com.sun.jndi.ldap.ControlFactory");
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put("java.naming.ldap.factory.socket", "com.vdi.dao.suport.DummySSLSocketFactory");
		environment.put(Context.PROVIDER_URL, "ldap://" + domainservername + ":" + port);
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, binddn);
		environment.put(Context.SECURITY_CREDENTIALS, bindpass);
		environment.put(Context.STATE_FACTORIES, "PersonStateFactory");
		environment.put(Context.OBJECT_FACTORIES, "PersonObjectFactory");
		environment.put(Context.REFERRAL,"ignore");
		environment.put(Context.SECURITY_PROTOCOL,"ssl");
		return new InitialDirContext(environment);
	}
	
	public static void connect(DirContext ctx,
		
			String searchbase
			) throws Exception {

//		LdapContextSource ldapContextSource = new LdapContextSource();
//		ldapContextSource.setBase(searchbase);
//		ldapContextSource.setUrl(domainservername);
//		ldapContextSource.setUserDn(binddn);
//		ldapContextSource.setPassword(bindpass);
//		ldapContextSource.afterPropertiesSet();
//		LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
//		System.out.println(ldapTemplate.search(
//				searchbase, "(objectCategory=organizationalUnit)",
//         new AttributesMapper() {
//            public Object mapFromAttributes(Attributes attrs)
//               throws NamingException {
//               return attrs.toString();
//            }
//         }));
		SearchControls ctls = new SearchControls();
//		String[] attrIDs = { "sAMAccountName", "name", "telephoneNumber", "mail", "streetAddress", "description" };
//		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//		String filter = "(|(objectCategory=user)(objectCategory=organizationalUnit))";
		String filter = "(objectClass=User)";
		NamingEnumeration<SearchResult> answer = ctx.search(searchbase, filter, ctls);
		try {
			while (answer.hasMore()) {
				SearchResult sr = answer.next();
				System.out.println();
				System.out.println("name==="+sr.getName());
			Attributes attrs = sr.getAttributes();
			NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll();
			while (attrEnum.hasMore()) {
				Attribute attr = attrEnum.next();
				System.out.println(attr.getID() + " = " + attr.get());
			}
			}
		} catch (PartialResultException e) {}
	}

}
