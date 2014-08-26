package com.vdi.gateway;

import static com.vdi.gateway.StringUtils.qstrep;

import java.io.InputStream;
public abstract class RuntimeUtils {
	

	private RuntimeUtils() {
	}

	public static int shell(StringBuilder result, String cmd, String... params) {
		if (isWindows()) {
			// For test only.
			return 0;
		}
		cmd = qstrep(cmd, "0-9a-zA-Z\\.", false, params);
		if (cmd == null) {
			return -1;
		}
		Runtime rt = Runtime.getRuntime();
		try {
			Process p = rt.exec(new String[] { "/bin/sh", "-c", cmd });
			InputStream is = p.getInputStream();
			InputStream es = p.getErrorStream();
			byte[] buff = new byte[4096];
			for (int read = -1; (read = is.read(buff)) > 0;) {
				if (result != null) {
					result.append(new String(buff, 0, read));
				}
			}
			StringBuilder error = new StringBuilder();
			for (int read = -1; (read = es.read(buff)) > 0;) {
				error.append(new String(buff, 0, read));
			}
			p.waitFor();
			int exitValue = p.exitValue();
			return exitValue;
		} catch (Exception e) {
			return -1;
		}
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows") > -1;
	}

}
