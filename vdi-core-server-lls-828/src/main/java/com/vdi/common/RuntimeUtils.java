package com.vdi.common;

import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.vdi.common.StringUtils.qstrep;
public abstract class RuntimeUtils {
	
	private static final Logger log = LoggerFactory.getLogger(RuntimeUtils.class);

	private RuntimeUtils() {
	}

	public static int shell(StringBuilder result, String cmd, String... params) {
		if (isWindows()) {
			// For test only.
			return 0;
		}
		cmd = qstrep(cmd, "0-9a-zA-Z\\.", false, params);
		if (cmd == null) {
			log.warn("Bad params for cmd '{}': {}", cmd, Arrays.toString(params));
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
					log.info(new String(buff, 0, read));
					result.append(new String(buff, 0, read));
				}
			}
			StringBuilder error = new StringBuilder();
			for (int read = -1; (read = es.read(buff)) > 0;) {
				error.append(new String(buff, 0, read));
			}
			String errorString = error.toString();
			if (errorString.length() > 0) {
				log.warn("Error in '{}': {}", cmd, errorString);
			} else {
				log.trace("Execution '{}' OK", cmd);
			}
			p.waitFor();
			// TODO Mapping error codes.
			int exitValue = p.exitValue();
      log.warn("exitValue: {}", exitValue);
			return exitValue;
		} catch (Exception e) {
			log.warn("Error on exec", e);
			// TODO Mapping error codes.
			return -1;
		}
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows") > -1;
	}

}
