package com;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;



/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate how to use "aes128-cbc".
 *
 */
import com.jcraft.jsch.*;

public class AES {
	public static void main(String[] arg) {

		try {
			JSch jsch = new JSch();

			// jsch.setKnownHosts("/home/foo/.ssh/known_hosts");

			String host = null;
			if (arg.length > 0) {
				host = arg[0];
			} else {
				host = "20.1.136.101";
			}
			String user = "root";

			Session session = jsch.getSession(user, host, 22);
			 session.setPassword("111111");
			 session.setConfig("StrictHostKeyChecking", "no");
			// username and password will be given via UserInfo interface.
//			UserInfo ui = new MyUserInfo();
//			session.setUserInfo(ui);

//			session.setConfig("cipher.s2c", "aes128-cbc,3des-cbc,blowfish-cbc");
//			session.setConfig("cipher.c2s", "aes128-cbc,3des-cbc,blowfish-cbc");
//			session.setConfig("CheckCiphers", "aes128-cbc");

			session.connect();

			Channel channel = session.openChannel("shell");
//			ByteArrayInputStream bytes=new ByteArrayInputStream("ls \r\n".getBytes());
//			InputStream in=new BufferedInputStream(bytes);
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			System.out.println("============================"+str);
			return true;
		}

		String passwd;

		// JTextField passwordField = (JTextField) new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			this.passwd = "123@qwe";
			return true;

		}

		public void showMessage(String message) {
			System.out.println("maxiaochao::::::::++++++>>>>>>>>"+message);
		}

		public String[] promptKeyboardInteractive(String arg0, String arg1,
				String arg2, String[] arg3, boolean[] arg4) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}