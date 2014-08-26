package com.spring;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.AttributesMapper;
import org.springframework.ldap.LdapTemplate;

import javax.naming.NamingException;
import javax.naming.directory.Attributes; 
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
public class Operator {
	private static  LdapTemplate template=null;
	public Operator(LdapTemplate template){
		this.template=template;
	}
	@SuppressWarnings("unchecked")
	/*
	 * uid 要查询人的uid
	 * ouName 上级dn
	 */
	public User getUserById(String uid,String ouName) {
	    String filter = "(&(objectclass=*)(uid=" + uid + "))";
	    List<User> list =(List<User>) template.search(ouName, filter,new UserAttributesMapper());
	    if (list.isEmpty()) {
	    	return null;	 
	    	}
	   System.out.println(list.size());
	    return list.get(0);
	}
	
	public boolean addUser(User vo) {
	    try {
	        // 基类设置
		BasicAttribute ocattr = new BasicAttribute("objectClass");
		ocattr.add("top");
//		ocattr.add("person");
//		ocattr.add("uidObject");
		ocattr.add("inetOrgPerson");
//		ocattr.add("organizationalPerson");
		// 用户属性
		Attributes attrs = new BasicAttributes();
		attrs.put(ocattr);
		attrs.put("cn", StringUtils.trimToEmpty(vo.getRealname()));
		attrs.put("sn", vo.getUsername());
		attrs.put("userPassword", vo.getPassword());
		template.bind("uid=" + vo.getUsername().trim()+","+vo.getOuName(), null, attrs);		
		return true;
	    } catch (Exception ex) {
		ex.printStackTrace();
		return false;
	    }
	}
	public boolean addOrganizationUnit(OrgUnit orgUnit) {
	    try {
	        // 基类设置
		BasicAttribute ocattr = new BasicAttribute("objectClass");
		ocattr.add("top");
		ocattr.add("organizationalUnit");
		// 用户属性
		Attributes attrs = new BasicAttributes();
		attrs.put(ocattr);
		attrs.put("ou", StringUtils.trimToEmpty(orgUnit.getOuName()));	
		template.bind("ou=" + orgUnit.getOuName().trim(), null, attrs);
		return true;
	    } catch (Exception ex) {
		ex.printStackTrace();
		return false;
	    }
	}
	
	public boolean updateUser(User vo) {
	    try {
		template.modifyAttributes("uid=" + vo.getUsername().trim()+","+vo.getOuName(), new ModificationItem[] {
		    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("cn", vo.getRealname().trim())),
		    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", vo.getUsername().trim())),
		    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", vo.getPassword().trim()))
		});
		return true;
	    } catch (Exception ex) {
		ex.printStackTrace();
		return false;
	    }
	}
	
	public boolean deleteUser(String username,String ouName) {
	    try {
		template.unbind("uid=" + username.trim()+","+ouName);
		return true;
	    } catch (Exception ex) {
		ex.printStackTrace();
		return false;
	    }
	}
	
	private class UserAttributesMapper implements AttributesMapper {  	  
        public Object mapFromAttributes(Attributes attrs) throws NamingException {  
            User user = new User();  
            user.setUsername((String) attrs.get("sn").get());  
            user.setRealname((String) attrs.get("cn").get());  
            
            if(attrs.get("userPassword")!=null){            	
            	  byte[] bytes=(byte[])attrs.get("userPassword").get(0);     
            	  user.setPassword(new String(bytes));
            }           
            return user;  
        }   
    }  
}

