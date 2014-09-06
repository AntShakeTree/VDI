package com.vdi.dao.suport;

import java.util.List;

import javax.naming.NamingException;

import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;


public class LdapJNDITest {

	public static void main(String[] args) throws NamingException {
		// TODO Auto-generated method stub
		UserMapBridge config=new UserMapBridge();
	  String base = "DC=cws,DC=com";
	  config.setBase(base);
//	  config.setPrincipal("administrator\\cws.com");
	  config.setPrincipal("cws\\administrator");
	  config.setUrl("ldaps://20.2.100.110:636");
	  config.setPassword("123.com");
      
      //��ѯDN��������Ϣ
//      ldap.findAllUser(config);
//      ldap.findAllOrganazations(config);
//      List<Organization> os=LdapJNDISupport.findAllOrganazations(config);
//  for (Organization string : os) {
//	String dns =string.getDistinguishedName();
//	String[] treename=dns.split("OU=");
//	for (String string2 : treename) {
//		System.out.println(string2+"==============================");
//	}
	String userdn =LdapHelp.getUserDn("maxiaochaouuuuu", "ou5", "DC=cws,DC=com");
	System.out.println(userdn);
	LdapSupport.createUser(config, userdn,"maxiaochaouuuuu","123.comBN");
	
  }
      
      //ɾ���û�
//      ldap.deleteUser(userDN);
       
      
      //����û�
//      ldap.addUser(userDN, userName, userPwd);
      
      //�޸�����
//      ldap.modifyPassword(userPwd, userDN);
      
      //�޸��û���
      //ldap.modifyUserName("Penghajun4", userDN);
     
      //�޸��û���Ϣ
       
//     Attributes attrs = new BasicAttributes(true);
//	   attrs.put("sAMAccountName", "Penghaijun5");
//     ldap.modifyUserInfo(attrs, userDN);
      
	}
