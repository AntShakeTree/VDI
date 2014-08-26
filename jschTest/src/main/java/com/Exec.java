package com;

/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate remote exec.
 *  $ CLASSPATH=.:../build javac Exec.java 
 *  $ CLASSPATH=.:../build java Exec
 * You will be asked username, hostname, displayname, passwd and command.
 * If everything works fine, given command will be invoked 
 * on the remote side and outputs will be printed out.
 *
 */
import com.jcraft.jsch.*;

import java.io.*;

public class Exec {
	public static void main(String[] arg) {
		try {
			JSch jsch = new JSch();

			String host = "20.1.136.10";
			
			String user = "root";
			Session session = jsch.getSession(user, host, 22);

			/*
			 * String xhost="127.0.0.1"; int xport=0; String
			 * display=JOptionPane.showInputDialog("Enter display name",
			 * xhost+":"+xport); xhost=display.substring(0,
			 * display.indexOf(':'));
			 * xport=Integer.parseInt(display.substring(display
			 * .indexOf(':')+1)); session.setX11Host(xhost);
			 * session.setX11Port(xport+6000);
			 */

			// username and password will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			String command = "ls";

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// X Forwarding
			// channel.setXForwarding(true);

			// channel.setInputStream(System.in);
			channel.setInputStream(null);

			// channel.setOutputStream(System.out);

			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			System.out.println(passwd);
			return passwd;
		}

		public boolean promptYesNo(String str) {
			System.out.println(str);
			return true;
		}

		String passwd;
	

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
		
			return true;
		}

		public boolean promptPassword(String message) {
			System.out.println("promptPassword"+message);
			passwd="1111111";
			
			return true;
		}

		public void showMessage(String message) {
		System.out.println(message);
		}



		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			return null;
		}
	}
}
