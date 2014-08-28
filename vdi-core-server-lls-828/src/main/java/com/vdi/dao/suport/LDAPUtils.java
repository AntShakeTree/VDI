//package com.vdi.dao.suport;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.Hashtable;
//
//import javax.naming.AuthenticationException;
//import javax.naming.Context;
//import javax.naming.NamingException;
//import javax.naming.directory.Attribute;
//import javax.naming.directory.Attributes;
//import javax.naming.directory.BasicAttribute;
//import javax.naming.directory.BasicAttributes;
//import javax.naming.directory.DirContext;
//import javax.naming.directory.ModificationItem;
//import javax.naming.ldap.Control;
//import javax.naming.ldap.InitialLdapContext;
//import javax.naming.ldap.LdapContext;
//import javax.net.SocketFactory;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//import org.springframework.util.StringUtils;
//
//import com.vdi.dao.user.domain.LdapConfig;
//import com.vdi.dao.user.domain.Organization;
//import com.vdi.dao.user.domain.User;
//
///**
// * 
// * ClassName: LDAPUtils <br/>
// * Function: ldap操作类. <br/>
// * date: 2014年8月17日 下午4:21:25 <br/>
// *
// * @author tree
// * @version
// * @since JDK 1.7
// */
//public abstract class LDAPUtils {
//
//	private LDAPUtils() {
//	}
//
//	public static void main(String[] args) throws Exception {
//		 LdapConfig config = new LdapConfig();
//		 config.setUrl("ldaps://20.2.100.110:636");
//		 config.setPrincipal("cn=Administrator,cn=Users,DC=cws,DC=com");
//		 config.setPassword("123.com");
//		 config.setBase("DC=CWS,DC=com");
//		 User user =new User();
//		 user.setUsername("yyyy6");
//		 user.setEmail("123@123.com");
//		 user.setPhone("13641137594");
//		 user.setRealname("yyyy6");
//		 user.setPassword("dddddd.com");
//		 
//		 
//		 
//		 
//		 
//		 Organization organization=new Organization();
//		 organization.setOrganizationname("ou5");
//		 user.setOrganization(organization);
////		 createOU(config, organization);
////		 createUser(config, user);
//		 addUser(config, Ldap.getUserDn(user.getUsername(), user.getOrganization().getOrganizationname(),config.getBase()), user.getUsername(), user.getPassword());
////		 updatePassword(config, user);
//	}
//	/**
//	 * 校验用户登录.
//	 * 
//	 * @param userDn
//	 *            String 用户DN
//	 * @param password
//	 *            String 用户密码
//	 * @return boolean
//	 */
//	public static boolean authenticate(LdapConfig config,User user) {
//		LdapContext ctx = null;
//		try {
//			ctx=createDirContext(config);
//			Control[] connCtls = new Control[] {};
//			ctx.getRequestControls();
//			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, Ldap.getUserDn(user.getUsername(),user.getOrganization().getOrganizationname() ,config.getBase() ));
//			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, user.getPassword());
//			ctx.reconnect(connCtls);
//			return true;
//		} catch (AuthenticationException e) {
//			return false;
//		} catch (NamingException e) {
//			e.printStackTrace();
//		} finally {
//			if (ctx != null) {
//				try {
//					ctx.close();
//				} catch (NamingException e) {
//					// TODO Auto-generated catch block
//					return false;
//				}
//				ctx = null;
//			}
//		}
//		return false;
//	}
//	private static LdapContext createDirContext(LdapConfig config)
//			throws NamingException {
//		String keystore = "D:\\Java\\jre7\\lib\\security\\cacerts";
////		String keystore = JRE+"//lib//security//cacerts";
//		System.setProperty("javax.net.ssl.trustStore", keystore);
//		Hashtable<String, String> environment = new Hashtable<String, String>();
////		environment.put(LdapContext.CONTROL_FACTORIES,
////				"com.sun.jndi.ldap.ControlFactory");
//		environment.put(Context.INITIAL_CONTEXT_FACTORY,
//				"com.sun.jndi.ldap.LdapCtxFactory");
//		environment.put(Context.PROVIDER_URL, config.getUrl());
//		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
//		environment.put(Context.SECURITY_PRINCIPAL, config.getPrincipal());
//		environment.put(Context.SECURITY_CREDENTIALS, config.getPassword());
//		environment.put(Context.SECURITY_PROTOCOL, "ssl");
//		environment.put("java.naming.ldap.version", "3");
//		environment.put(Context.REFERRAL, "ignore");
////		environment.put("java.naming.ldap.factory.socket", VDISSLSocketFactory.class.getName());
//		return new InitialLdapContext(environment, null);
//
//	}
//
//	/**
//	 * 
//	 * createUser: <br/>
//	 * 
//	 * @author tree
//	 * @param ctx
//	 * @param organiztionname
//	 * @param username
//	 * @param realname
//	 * @param password
//	 * @param basedomain
//	 * @throws NamingException
//	 * @throws UnsupportedEncodingException 
//	 * @since JDK 1.7
//	 */
//	public static void createUser(LdapConfig config, User user)
//			throws NamingException, UnsupportedEncodingException {
//		LdapContext ctx = createDirContext(config);
//		try {
//			String realname = user.getRealname();
//			String username = user.getUsername();
//			String basedomain = config.getBase();
//			String email = user.getEmail();
//			String phone = user.getPhone();
//			String organiztionname = user.getOrganization()
//					.getOrganizationname();
//			String distinguishedName = Ldap.getUserDn(username,
//					organiztionname, basedomain);
//			Attributes newAttributes = new BasicAttributes(true);
//			Attribute oc = new BasicAttribute(Ldap.OBJECT_CLASS);
//			oc.add(Ldap.TOP);
//			oc.add(Ldap.OBJECT_CLASS_PERSON);
//			oc.add(Ldap.OBJECT_CLASS_ORGANIZATION_PERSON);
//			oc.add(Ldap.OBJECT_CLASS_USER);
//			newAttributes.put(oc);
//			newAttributes.put(new BasicAttribute(Ldap.USERNAME, username));
//			newAttributes.put(new BasicAttribute(Ldap.SAMANAME, username));
//			if (!StringUtils.isEmpty(realname)) {
//				newAttributes.put(new BasicAttribute(Ldap.GIVENNAME, realname));
//			}
//			if (!StringUtils.isEmpty(email)){
//				newAttributes.put(new BasicAttribute(Ldap.E_MAIL, email));
//			}
//			if (!StringUtils.isEmpty(phone)){
//				newAttributes.put(new BasicAttribute(Ldap.MOBILE, phone));
//			}
//			//一开始不能建正常的用户
//			newAttributes.put(
//					Ldap.USERACCOUNTCONTROL,
//					Integer.toString(Ldap.UF_NORMAL_ACCOUNT + Ldap.UF_PASSWD_NOTREQD
//							+ Ldap.UF_PASSWORD_EXPIRED + Ldap.UF_ACCOUNTDISABLE));
//			ctx.createSubcontext(distinguishedName, newAttributes);
//			ModificationItem[] mods = new ModificationItem[2];
//			// Replace the "unicdodePwd" attribute with a new value
//			// Password must be both Unicode and a quoted string
//			String newQuotedPassword = "\"" + user.getPassword() + "\"";
//			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
//			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//					new BasicAttribute("unicodePwd", newUnicodePassword));
//			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//					new BasicAttribute("userAccountControl",
//							Integer.toString(Ldap.UF_NORMAL_ACCOUNT
//									+ Ldap.UF_PASSWORD_EXPIRED)));
//			// update
//			ctx.modifyAttributes(distinguishedName, mods);
//			
//			
//		} finally {
//			ctx.close();
//		}
//	}
//
//	/**
//	 * 
//	 * createOU: <br/>
//	 * 
//	 * @author tree
//	 * @param ctx
//	 * @param organizationname
//	 * @param basedomain
//	 * @throws NamingException
//	 * @since JDK 1.7
//	 */
//	public static void createOU(LdapConfig config, Organization organization)
//			throws NamingException {
//		LdapContext ctx = createDirContext(config);
//		try {
//			Attributes create = new BasicAttributes(true);
//			Attribute objectclass = new BasicAttribute(Ldap.OBJECT_CLASS);
//			objectclass.add(Ldap.TOP);
//			objectclass.add(Ldap.OBJECT_CLASS_ORGANIZATION);
//			create.put(objectclass);
//			create.put(new BasicAttribute(Ldap.OU, organization
//					.getOrganizationname()));
//			ctx.createSubcontext(
//					Ldap.getOUDn(organization.getOrganizationname(),
//							config.getBase()), create);
//		} finally {
//			ctx.close();
//		}
//	}
//
//	/**
//	 * 修改密码
//	 * 
//	 * @throws NamingException
//	 */
//	public static void updatePassword(LdapConfig config, User user) throws NamingException
//		 {
//	    LdapContext ctx = null;  
//	    try {  
//	    	ctx=createDirContext(config);
//				ModificationItem[] mods = new ModificationItem[2];
//				String newQuotedPassword = "\"" + user.getPassword() + "\"";
//				byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
//				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//						new BasicAttribute(Ldap.PASSWORD, newUnicodePassword));
//				mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//				new BasicAttribute(Ldap.USERACCOUNTCONTROL,
//							Integer.toString(Ldap.UF_PASSWORD_NEVER_EXPIRED)));
//				ctx.modifyAttributes("cn=yyyy,OU=ou5,DC=cws,DC=com", mods);
//			
//			
////			ctx=createDirContext(config);
////			String password = user.getPassword();
////			System.out.println(password);
////			ModificationItem[] mods = new ModificationItem[1];
////			password = "\"" + password + "\"";
////
////			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
////					new BasicAttribute(Ldap.PASSWORD,
////							password.getBytes("UTF-16LE")));
//////			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//////					new BasicAttribute(Ldap.USERACCOUNTCONTROL,
//////							Integer.toString(Ldap.UF_PASSWORD_NEVER_EXPIRED)));
////
////			ctx.modifyAttributes(
////					Ldap.getUserDn(user.getUsername(), user.getOrganization()
////							.getOrganizationname(), config.getBase()), mods);
//			mods=null;
//	    } catch (NamingException e) { 
//	    	e.printStackTrace();
//	    } catch (Exception e) {  
//	        e.printStackTrace();  
//	    }finally {  
//	        if (ctx != null) {  
//	            try {  
//	                ctx.close();  
//	            } catch (NamingException e) {  
//	                e.printStackTrace();  
//	            }  
//	            ctx = null;  
//	        }  
//	    }
//	    
////	    } catch (Exception e) {
////			e.printStackTrace();
////		} finally {  
////	        if (ctx != null) {  
////	            try {  
////	                ctx.close();  
////	            } catch (NamingException e) {  
////	                e.printStackTrace();  
////	            }  
////	            ctx = null;  
////	        }  
////	    }
//	    
//		 
//		   
//	    
//	}
//
//	public static void deleteUser(LdapConfig config, User user)
//			throws NamingException {
//		LdapContext ctx = createDirContext(config);
//		try {
//			ctx.destroySubcontext(Ldap.getUserDn(user.getUsername(),
//					user.getUsername(), config.getBase()));
//		} finally {
//			ctx.close();
//		}
//	}
//
//	public static void deleteOrganiztion(LdapConfig config, Organization org)
//			throws NamingException {
//		LdapContext ctx = createDirContext(config);
//		try {
//			ctx.destroySubcontext(Ldap.getOUDn(org.getOrganizationname(),
//					config.getBase()));
//		} finally {
//			ctx.close();
//		}
//	}
//
//	public static class Ldap {
//		public static final String USERACCOUNTCONTROL = "userAccountControl";
//		public static String TOP = "top";
//		public static String SAMANAME = "sAMAccountName";
//		public static String USERNAME = "CN";
//		public static String c="DC";
//		public static String PASSWORD = "unicodePwd";
//		public static String PLAIN_PASSWORD = "password";
//		public static String E_MAIL = "mail";
//		public static String MOBILE = "mobile";
//		public static String SURNAME = "sn";
//		public static String GIVENNAME = "givenname";
//		public static String OU = "OU";
//		public static String OBJECT_CLASS_ORGANIZATION_PERSON = "organizationalPerson";
//		public static String OBJECT_CLASS_PERSON = "person";
//		public static String OBJECT_CLASS_USER = "user";
//		public static String OBJECT_CLASS = "objectclass";
//		public static String OBJECT_CLASS_ORGANIZATION = "organizationalUnit";
//		public static int UF_ACCOUNTDISABLE = 0x0002;
//		public static int UF_PASSWD_NOTREQD = 0x0020;
//		public static int UF_PASSWD_CANT_CHANGE = 0x0040;
//		private static int UF_NORMAL_ACCOUNT = 0x0200;
//		public static int UF_DONT_EXPIRE_PASSWD = 0x10000;
//		private static int UF_PASSWORD_EXPIRED = 0x800000;
//		private static int UF_PASSWORD_NEVER_EXPIRED = 0x10200;
//
//		public static String getUserDn(String username, String oun,
//				String basedomain) {
//			return Ldap.USERNAME+"="+username+","+OU + "=" + oun + "," + basedomain;
//		}
//
//		public static String getOUDn(String oun, String basedomain) {
//			return OU + "=" + oun + "," + basedomain;
//		}
//	}
//
//	public static class VDISSLSocketFactory extends SSLSocketFactory {
//
//		private SSLSocketFactory factory;
//
//		public VDISSLSocketFactory() {
//
//			try {
//
//				SSLContext sslcontext = SSLContext.getInstance("SSL");
//
//				sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
//							@Override
//						    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
//						            throws CertificateException {
//						    }
//
//						    @Override
//						    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
//						            throws CertificateException {
//						        
//						    }
//						    @Override
//						    public X509Certificate[] getAcceptedIssuers() {
//						        return new X509Certificate[0];
//						    }	
//						} },
//
//						new java.security.SecureRandom());
//
//				factory = (SSLSocketFactory) sslcontext.getSocketFactory();
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//
//		}
//
//		public static SocketFactory getDefault() {
//
//			return new VDISSLSocketFactory();
//
//		}
//
//		public Socket createSocket(Socket socket, String s, int i, boolean flag)
//				throws IOException {
//
//			return factory.createSocket(socket, s, i, flag);
//
//		}
//
//		public Socket createSocket(InetAddress inaddr, int i,
//				InetAddress inaddr1, int j) throws IOException {
//
//			return factory.createSocket(inaddr, i, inaddr1, j);
//
//		}
//
//		public Socket createSocket(InetAddress inaddr, int i)
//				throws IOException {
//
//			return factory.createSocket(inaddr, i);
//
//		}
//
//		public Socket createSocket(String s, int i, InetAddress inaddr, int j)
//				throws IOException {
//
//			return factory.createSocket(s, i, inaddr, j);
//
//		}
//
//		public Socket createSocket(String s, int i) throws IOException {
//
//			return factory.createSocket(s, i);
//
//		}
//
//		public String[] getDefaultCipherSuites() {
//
//			return factory.getSupportedCipherSuites();
//
//		}
//
//		public String[] getSupportedCipherSuites() {
//
//			return factory.getSupportedCipherSuites();
//
//		}
//	}
//public static boolean addUser(LdapConfig config,String userDN, String userName, String userPwd) {
//		
//		LdapContext ctx = null;
//		try {
//			
//			ctx = createDirContext(config);
//			userDN = "cn="+userName+","+userDN;
//			Attributes attrs = new BasicAttributes(true);
//			attrs.put("objectClass", "user");
//			attrs.put("sAMAccountName", userName);
//			attrs.put("cn", userName);
//			attrs.put(
//					"userAccountControl",
//					Integer.toString(Ldap.UF_NORMAL_ACCOUNT + Ldap.UF_PASSWD_NOTREQD
//							+ Ldap.UF_PASSWORD_EXPIRED + Ldap.UF_ACCOUNTDISABLE));
//			// Create the context
//			ctx.createSubcontext(userDN, attrs);
//			ModificationItem[] mods = new ModificationItem[2];
//			// Replace the "unicdodePwd" attribute with a new value
//			// Password must be both Unicode and a quoted string
//			String newQuotedPassword = "\"" + userPwd + "\"";
//			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
//			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//					new BasicAttribute("unicodePwd", newUnicodePassword));
//			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
//					new BasicAttribute("userAccountControl",
//							Integer.toString(Ldap.UF_NORMAL_ACCOUNT
//									+ Ldap.UF_PASSWORD_EXPIRED)));
//			// update
//			ctx.modifyAttributes(userDN, mods);
//			
//			mods = null;
//			return true;
//		} catch (NamingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (ctx != null) {
//				try {
//					ctx.close();
//				} catch (NamingException e) {
//					// TODO Auto-generated catch block
//					return false;
//				}
//				ctx = null;
//			}
//		}
//		return false;
//	}
//}
