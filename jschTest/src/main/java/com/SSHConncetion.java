package com;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConncetion {
	private Session session;
	private ChannelExec exec;
	private String host, username, password;

	public SSHConncetion() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

	public boolean connect() {
		if (!atomicBoolean.get()) {
			JSch jsch = new JSch();
			try {
				session = jsch.getSession(username, host, 22);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(password);
				session.connect(10000);
				exec = (ChannelExec) session.openChannel("exec");
				atomicBoolean.set(session.isConnected());
			} catch (JSchException e) {
				e.printStackTrace();
				atomicBoolean.set(false);
			}
		}
		return atomicBoolean.get();
	}



	public String exec(String command) {
		try {
			StringBuilder sb = new StringBuilder("");
			if (connect()) {
				exec.setCommand(command);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						exec.getInputStream());
				byte[] bytes = new byte[4096];
				exec.connect(10000);
				while (true) {
					while (bufferedInputStream.available() > 0) {
						int i = bufferedInputStream
								.read(bytes, 0, bytes.length);
						if (i < 0)
							break;
						sb.append(new String(bytes, 0, i));
					}
					if (exec.isClosed()) {
						if (bufferedInputStream.available() > 0)
							continue;
						break;
					}
				}
			}
			return sb.toString();
		} catch (IOException e) {

		} catch (JSchException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		if (atomicBoolean.get()) {
			if (exec != null && session != null) {
				exec.disconnect();
				session.disconnect();
				atomicBoolean.set(false);
			}
		}
	}

	public SSHConncetion build(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
		return this;
	}
}
