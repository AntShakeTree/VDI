package com.vdi.dao.suport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;

public class LdapJNDI {
	protected static Logger log = org.slf4j.LoggerFactory
			.getLogger(LdapJNDI.class);
	private static int UF_ACCOUNTDISABLE = 0x0002;
	private static int UF_PASSWD_NOTREQD = 0x0020;
	private static int UF_NORMAL_ACCOUNT = 0x0200;
	private static int UF_PASSWORD_EXPIRED = 0x800000;

	public static LdapContext createDirContext(LdapConfig config)
			throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, config.getUrl());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, config.getPrincipal());
		env.put(Context.SECURITY_CREDENTIALS, config.getPassword());
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put(Context.REFERRAL, "follow");// ~设成其他值不到属性会报错
		env.put("java.naming.ldap.factory.socket",
				VDISSLSocketFactory.class.getName());
		return new InitialLdapContext(env, null);
	}

	public boolean authenticate(LdapConfig config, String userDn,
			String password) {
		LdapContext ctx = null;
		try {
			Control[] connCtls = new Control[] {};
			ctx = createDirContext(config);
			ctx.getRequestControls();
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDn);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			return true;
		} catch (AuthenticationException e) {
			return false;
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					return false;
				}
				ctx = null;
			}
		}
		return false;
	}

	// 根据组来查询所有用户
	public List<User> findUsers(LdapConfig config, String... orgnazationname) {
		LdapContext ctx = null;
		List<User> users = new ArrayList<User>();
		try {
			ctx = createDirContext(config);
			NamingEnumeration<SearchResult> searchResult = ctx.search(
					config.getBase(), LdapHelp.FILLTER_USER, null);
			SearchResult result = null;

			while (searchResult.hasMore()) {
				result = searchResult.next();
				Attributes attributes = result.getAttributes();
				User user = LdapHelp.bulidUser(attributes);
				users.add(user);
			}

		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return users;
	}

	public static List<Organization> findAllOrganazations(LdapConfig config)
			throws NamingException {
		LdapContext ctx = null;
		List<Organization> list = new ArrayList<Organization>();
		String searchbase = config.getBase();
		SearchControls ctls = new SearchControls(); 
		String[] attrIDs = { "objectGUID", "distinguishedName", "OU" };
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(|(objectCategory=groupPolicyContainer)(|(objectCategory=container)(objectCategory=organizationalUnit)))";
		NamingEnumeration<SearchResult> answer = null;
		try {
			ctx = createDirContext(config);
			answer = ctx.search(searchbase, filter, ctls);
		} catch (NameNotFoundException e) {
			log.warn("{} not found.", searchbase);
			// throw e;
			return null;
		}
		SearchResult result = null;

		try {
			AtomicBoolean  isEnd=new AtomicBoolean(true);
			while (answer.hasMore()&&isEnd.get()) {
				// answer.next();
				result = answer.next();
				Attributes attributes = result.getAttributes();
				Organization organization = LdapHelp
						.buildOrganzation(attributes,isEnd);
				if(organization!=null)
				list.add(organization);
			}
		} catch (PartialResultException e) {
		} finally {
			ctx.close();
		}
		return list;

	}

//	private static void recursiveQueryOrgnazations(LdapContext ctx,
//			Organization root, List<Organization> os) throws NamingException {
//		if (os == null) {
//			os = new ArrayList<Organization>();
//		}
//		String searchbase = root.getDistinguishedName();
//		SearchControls ctls = new SearchControls();
//		String[] attrIDs = { "objectGUID", "distinguishedName", "OU" };
//		ctls.setReturningAttributes(attrIDs);
//		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//		String filter = "(|(objectCategory=groupPolicyContainer)(|(objectCategory=container)(objectCategory=organizationalUnit)))";
//		NamingEnumeration<SearchResult> answer = null;
//		try {
//			answer = ctx.search(searchbase, filter, ctls);
//		} catch (NameNotFoundException e) {
//			log.warn("{} not found.", searchbase);
//
//			return;
//		}
//		SearchResult result = null;
//
//		try {
//			while (answer.hasMore()) {
//				// answer.next();
//				result = answer.next();
//				Attributes attributes = result.getAttributes();
//				Organization organization = LdapHelp
//						.buildOrganzation(attributes);
//				organization.setParent(root);
//				os.add(organization);
//				recursiveQueryOrgnazations(ctx, organization, os);
//			}
//		} catch (PartialResultException e) {
//		} finally {
//			if (ctx != null) {
//				try {
//					ctx.close();
//				} catch (NamingException e) {
//				}
//				ctx = null;
//			}
//		}
//	}

	public boolean addUser(LdapConfig config, String userDN, String userName,
			String userPwd) {

		LdapContext ctx = null;
		try {

			ctx = createDirContext(config);
			;
			userDN = "cn=" + userName + "," + userDN;
			Attributes attrs = new BasicAttributes(true);
			attrs.put("objectClass", "user");
			attrs.put("sAMAccountName", userName);
			attrs.put("cn", userName);
			attrs.put(
					"userAccountControl",
					Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD
							+ UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));
			// Create the context
			ctx.createSubcontext(userDN, attrs);
			ModificationItem[] mods = new ModificationItem[2];
			// Replace the "unicdodePwd" attribute with a new value
			// Password must be both Unicode and a quoted string
			String newQuotedPassword = "\"" + userPwd + "\"";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("userAccountControl",
							Integer.toString(UF_NORMAL_ACCOUNT
									+ UF_PASSWORD_EXPIRED)));
			// update
			ctx.modifyAttributes(userDN, mods);

			mods = null;
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					return false;
				}
				ctx = null;
			}
		}
		return false;
	}

	public boolean modifyPassword(LdapConfig config, String userPwd,
			String userDN) {
		LdapContext ctx = null;
		try {
			ctx = createDirContext(config);

			ModificationItem[] mods = new ModificationItem[2];
			String newQuotedPassword = "\"" + userPwd + "\"";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			// update

			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("userAccountControl",
							Integer.toString(UF_NORMAL_ACCOUNT
									+ UF_PASSWORD_EXPIRED)));

			ctx.modifyAttributes(userDN, mods);
			mods = null;
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}

	// public boolean modifyUserName(String userName, String userDN) {
	// LdapContext ctx = null;
	// try {
	//
	// ctx = createDirContext(config);
	// ModificationItem[] mods = new ModificationItem[1];
	// mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
	// new BasicAttribute("sAMAccountName", userName));
	// //update
	// ctx.modifyAttributes(userDN, mods);
	// mods = null;
	// return true;
	// } catch (NamingException e) {
	// return false;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }finally {
	// if (ctx != null) {
	// try {
	// ctx.close();
	// } catch (NamingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// ctx = null;
	// }
	// }
	// return false;
	// }
	public boolean deleteUser(LdapConfig config, String userDN) {
		LdapContext ctx = null;
		try {

			ctx = createDirContext(config);
			ctx.destroySubcontext(userDN);
			return true;
		} catch (NamingException e) {
			return false;
		} catch (Exception e) {
			System.err.println("Problem: " + e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return false;
	}

	//
	// public boolean modifyUserInfo(Attributes attrs, String userDN) {
	// LdapContext ctx = null;
	// try {
	// ctx = createDirContext(config);
	// ctx.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, attrs);
	// return true;
	// } catch (NamingException e) {
	// return false;
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (ctx != null) {
	// try {
	// ctx.close();
	// } catch (NamingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// ctx = null;
	// }
	// }
	// return false;
	// }

	public static class VDISSLSocketFactory extends SSLSocketFactory {

		private SSLSocketFactory factory;

		public VDISSLSocketFactory() {

			try {

				SSLContext sslcontext = SSLContext.getInstance("SSL");

				sslcontext.init(null,
						new TrustManager[] { new X509TrustManager() {
							@Override
							public void checkClientTrusted(
									X509Certificate[] arg0, String arg1)
									throws CertificateException {
							}

							@Override
							public void checkServerTrusted(
									X509Certificate[] arg0, String arg1)
									throws CertificateException {

							}

							@Override
							public X509Certificate[] getAcceptedIssuers() {
								return new X509Certificate[0];
							}
						} },

						new java.security.SecureRandom());

				factory = (SSLSocketFactory) sslcontext.getSocketFactory();

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		public static SocketFactory getDefault() {

			return new VDISSLSocketFactory();

		}

		public Socket createSocket(Socket socket, String s, int i, boolean flag)
				throws IOException {

			return factory.createSocket(socket, s, i, flag);

		}

		public Socket createSocket(InetAddress inaddr, int i,
				InetAddress inaddr1, int j) throws IOException {

			return factory.createSocket(inaddr, i, inaddr1, j);

		}

		public Socket createSocket(InetAddress inaddr, int i)
				throws IOException {

			return factory.createSocket(inaddr, i);

		}

		public Socket createSocket(String s, int i, InetAddress inaddr, int j)
				throws IOException {

			return factory.createSocket(s, i, inaddr, j);

		}

		public Socket createSocket(String s, int i) throws IOException {

			return factory.createSocket(s, i);

		}

		public String[] getDefaultCipherSuites() {

			return factory.getSupportedCipherSuites();

		}

		public String[] getSupportedCipherSuites() {

			return factory.getSupportedCipherSuites();

		}
	}

}
