package com.opzoon.vdi.core.util;

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

public abstract class LDAPUtils {
	
	private LDAPUtils () {}
	
	public static void main(String[] args) throws Exception {
//		connect("ldap://192.168.100.100:389", "dc=opzoondev,dc=com", "opzoondev\\morigen", "123@qwe");
		connect("ldap://20.2.100.200:389", "ou=4,dc=txr,dc=com,dc=cn", "txr\\administrator", "456.com");
	}

	public static DirContext createDirContext(
			String domainservername,
			int port,
			String binddn,
			String bindpass) throws NamingException {
		StringBuilder ipSB = new StringBuilder();
		RuntimeUtils.shell(ipSB, "ping -c 1 " + domainservername + "|head -n 1|awk '{print $3}'|sed -e 's/(//g'|sed -e 's/)//g'");
		String ip = ipSB.toString();
		if (ip.length() < 1) {
			throw new NamingException("Ping failed");
		}
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(LdapContext.CONTROL_FACTORIES, "com.sun.jndi.ldap.ControlFactory");
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, "ldap://" + ip + ":" + port);
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, binddn);
		environment.put(Context.SECURITY_CREDENTIALS, bindpass);
		environment.put(Context.STATE_FACTORIES, "PersonStateFactory");
		environment.put(Context.OBJECT_FACTORIES, "PersonObjectFactory");
		return new InitialDirContext(environment);
	}
	
	public static void connect(
			String domainservername,
			String searchbase,
			String binddn,
			String bindpass) throws Exception {
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(LdapContext.CONTROL_FACTORIES, "com.sun.jndi.ldap.ControlFactory");
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, domainservername);
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, binddn);
		environment.put(Context.SECURITY_CREDENTIALS, bindpass);
		environment.put(Context.STATE_FACTORIES, "PersonStateFactory");
		environment.put(Context.OBJECT_FACTORIES, "PersonObjectFactory");
//		environment.put(Context.REFERRAL, "follow");
		DirContext ctx = new InitialDirContext(environment);
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
		String filter = "(objectCategory=*)";
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
