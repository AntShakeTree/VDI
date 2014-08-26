package com.spring;

public class Test {

	public static void main(String[] args) {
		
	Operator op=new Operator(LdapTemplateUtil.getLdapTemplate(
					"ldap://127.0.0.1:389",
					"dc=opzoon,dc=com", 
					"secret", 
					"cn=Manager,dc=opzoon,dc=com")
					);
/*add*/	
//		if(op.addUser(new User("maozhaoyuan","hongcao","123456"))){
//			System.out.println("add success");
//		};
	
/*search*/
	
//		User user=op.getUserById("hongcao");
//		System.out.println(user.getRealname()+" "+user.getUsername()+" "+user.getPassword());

	/*update*/
//		User user=new User("mao", "hongcao","123");
//		if(op.updateUser(user)){
//			System.out.println("update success");
//		};
	/*delete*/
//		if(op.deleteUser("hongcao")){
//			System.out.println("delete success");
//		};		
        
	}
}
