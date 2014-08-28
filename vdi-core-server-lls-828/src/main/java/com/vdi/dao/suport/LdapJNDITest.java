package com.vdi.dao.suport;

import java.util.List;

import javax.naming.NamingException;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;


public class LdapJNDITest {

	public static void main(String[] args) throws NamingException {
		// TODO Auto-generated method stub
		LdapConfig config=new LdapConfig();
	  String base = "DC=cws,DC=com";
	  config.setBase(base);
//	  config.setPrincipal("administrator\\cws.com");
	  config.setPrincipal("cn=Administrator,cn=Users,dc=cws,dc=com");
	  config.setUrl("ldaps://20.2.100.110:636");
	  config.setPassword("123.com");
      
      //��ѯDN��������Ϣ
//      ldap.findAllUser(config);
//      ldap.findAllOrganazations(config);
      List<Organization> os=LdapJNDI.findAllOrganazations(config);
  for (Organization string : os) {
	String dns =string.getDistinguishedName();
	String[] treename=dns.split("OU=");
	for (String string2 : treename) {
		System.out.println(string2+"==============================");
	}
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
}
