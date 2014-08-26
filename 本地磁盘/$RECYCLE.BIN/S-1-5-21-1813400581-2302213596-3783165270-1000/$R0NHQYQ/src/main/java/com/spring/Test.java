package com.spring;

public class Test {

	public static void main(String[] args) {
		
	Operator op=new Operator(LdapTemplateUtil.getLdapTemplate(
					"ldap://127.0.0.1:389",
					"o=opzoon,c=com", 
					"secret", 
					"cn=Manager,o=opzoon,c=com")
					);
	
	/*ldapadd -x -D "cn=manager,o=opzoon,c=com" -w secret -f initial.ldif*/
	
	/*add ou*/	
	if(op.addOrganizationUnit(new OrgUnit("develop"))){
		System.out.println("add OrganizationUnit success");
	};
	
  /*add user*/
//	if(op.addUser(new User("maozhaoyuan","hongcao","123456","ou=develop"))){
//		System.out.println("add user success");
//	};
			
/*search*/
	
//	User user=op.getUserById("hongcao","ou=develop");
//		System.out.println(user.getRealname()+" "+user.getUsername()+" "+user.getPassword());

	/*update*/
//		User newUser=new User("mao", "hongcao","123","ou=develop");
//		if(op.updateUser(newUser)){
//			System.out.println("update success");
//		};
		
	/*delete*/
//		if(op.deleteUser("hongcao","ou=develop")){
//			System.out.println("delete success");
//		};		
        
	}
}
