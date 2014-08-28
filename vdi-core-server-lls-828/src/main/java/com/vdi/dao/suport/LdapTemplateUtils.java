/**
 * Project Name:vdi-core-server-lls
 * File Name:LdapTemplateUtils.java
 * Package Name:com.vdi.dao.suport
 * Date:2014年8月25日上午11:00:54
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.suport;

import java.util.List;

import javax.naming.directory.Attributes;

import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import com.vdi.dao.user.domain.User;

public class LdapTemplateUtils {
	public static void main(String[] args) {
		LdapContextSource ctx =new LdapContextSource();
		ctx.setBase("OU=研发管理部,DC=cws,DC=com");
		ctx.setAuthenticationSource(new AuthenticationSource() {
			 @Override
			 public String getCredentials() {
			 return "123.com";
			 }
			  
			 @Override
			 public String getPrincipal() {
			 return "cn=Administrator,cn=Users,dc=cws,dc=com";
			 }
			 });
		ctx.setUrl("ldaps://20.2.100.110:636");
//		ctx.
		LdapTemplate	ldapTemplate=new LdapTemplate(ctx);
//		ldapTemplate.f
	}
//	   private class PersonAttributesMapper implements AttributesMapper<User> {
//		      public User mapFromAttributes(Attributes attrs) throws NamingException {
//		    	  User person = new User();
//		         person.setUsername((String)attrs.get("cn").get());
//		         person.setMobile((String)attrs.get("mobile").get());
////		         byte[] bytes= attrs.get("objectGUID").get().toString().getBytes();
////		         person.setDomain(domain);
//		         person.setGuid(new String(attrs.get("objectGUID").get()));
//		         return person;
//		      }
//		   }
//
//		   public List<Person> getAllPersons() {
//		      return ldapTemplate.search(query()
//		          .where("objectclass").is("person"), new PersonAttributesMapper());
//		   }
}
