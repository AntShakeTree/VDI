/**
 * Project Name:vdi-core-server-lls
 * File Name:LdapHelp.java
 * Package Name:com.vdi.dao.suport
 * Date:2014年8月25日下午6:03:34
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.suport;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import sun.misc.BASE64Encoder;

import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;

public  class LdapHelp {
	public static final String USERACCOUNTCONTROL = "userAccountControl";
	public static String TOP = "top";
	public static String SAMANAME = "sAMAccountName";
	public static String USERNAME = "CN";
	public static String c = "DC";
	public static String PASSWORD = "unicodePwd";
	public static String PLAIN_PASSWORD = "password";
	public static String E_MAIL = "mail";
	public static String MOBILE = "mobile";
	public static String SURNAME = "sn";
	public static String GIVENNAME = "givenname";
	public static String OU = "ou";
	public static String OBJECT_CLASS_ORGANIZATION_PERSON = "organizationalPerson";
	public static String OBJECT_CLASS_PERSON = "person";
	public static String OBJECT_CLASS_USER = "user";
	public static String OBJECT_CLASS = "objectclass";
	public static String OBJECT_CLASS_ORGANIZATION = "organizationalUnit";
	public static int UF_ACCOUNTDISABLE = 0x0002;
	public static int UF_PASSWD_NOTREQD = 0x0020;
	public static int UF_PASSWD_CANT_CHANGE = 0x0040;
	public static int UF_DONT_EXPIRE_PASSWD = 0x10000;
	public static int UF_PASSWORD_EXPIRED = 0x800000;
	public static int UF_PASSWORD_NEVER_EXPIRED = 0x10200;
	public static String FILLTER_ORGNAZATION = "(|(objectCategory=groupPolicyContainer)(|(objectCategory=container)(objectCategory=organizationalUnit)))";
	public static String FILLTER_USER = "(&(objectCategory=user)(objectClass=user))";
	public static final String DOMAIN_BINDDN="objectGUID";
	

	public static String getUserDn(String username, String oun,
			String basedomain) {
		return LdapHelp.USERNAME + "=" + username + "," + OU + "=" + oun + ","
				+ basedomain;
	}

	public static String getOUDn(String oun, String basedomain) {
		return OU + "=" + oun + "," + basedomain;
	}

	public static User bulidUser(Attributes attrs) {
		User user = new User();
		user.setUsername(attrs.get(USERNAME).toString());
		user.setEmail(attrs.get(E_MAIL) + "");
		user.setEnabled(true);
		user.setMobile(attrs.get(MOBILE) + "");
		user.setRealname(attrs.get(SURNAME) + "" + attrs.get(GIVENNAME));
		user.setTelephone(attrs.get("telephoneNumber") + "");
		user.setAddress(attrs.get("streetAddress") + "");
		user.setNotes(attrs.get("description") + "");

		return user;
	}

	public static Domain buildDomain(LdapConfig config,Domain domain, Attributes attributes) throws NamingException {
		domain.setAccesstype(config.getAccesstype());
		domain.setAddress(config.getAddress());
		domain.setDns(config.getDns());
		domain.setDomainbinddn(config.getBase());
		domain.setPrincipal(config.getPrincipal());
		domain.setDomainbinddn(config.getBase());
		domain.setDomainbindpass(config.getPassword());
		domain.setDomainname(findDomainName(config.getBase()));
		domain.setDomaintype(Domain.DOMAIN_TYPE_MSAD);
		Object pwdps=attributes.get("pwdProperties").get();
		if("1".equals(pwdps+"")){
			domain.setPasswordpolicy(true);	
		}else{
			domain.setPasswordpolicy(false);
		}
		domain.setGuid(getGUID(attributes));
		domain.setDomainbinddn(attributes.get("distinguishedName").get()+"");
		//int passwordlen=0;
	//System.err.print(attributes.get("minPwdLength").get());	
		int passwordlen=Integer.parseInt(attributes.get("minPwdLength").get()+"");
		domain.setPasswordlen(passwordlen);
		return domain;
	}

	private static String findDomainName(String searchbase) {
		String rootSearchBase = searchbase.substring(searchbase
				.toLowerCase().indexOf("dc=") + 3);
		rootSearchBase = rootSearchBase.replace(",dc=", ".");
		return rootSearchBase;
	}

	public static Organization buildOrganzation(Attributes attributes, AtomicBoolean isEnd) {
		Organization organization = new Organization();
		Object ou =attributes.get(OU);
		if(ou==null){
			isEnd.compareAndSet(isEnd.get(), false);
			return null;
		}
		String fullname =OU.toString().toLowerCase().trim();
		organization.setOrganizationname(fullname.substring(fullname.lastIndexOf("ou=")+3));
		System.out.println(organization.getOrganizationname());
		organization.setBinddn(attributes.get("distinguishedName")+"");
		organization.setGuid(getGUID(attributes));
		return organization;
	}

	//

	private static String getGUID(Attributes attributes) {
		Object guid = attributes.get("objectGUID");
		try {
			return new BASE64Encoder().encode(guid.toString().getBytes(
					"ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}