package com.opzoon.vdi.core.util;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggableInputStream extends InputStream {
	
	private static final Logger log = LoggerFactory.getLogger(LoggableInputStream.class);
	
	private final InputStream is;
	
	private final StringBuilder sb;

	public LoggableInputStream(InputStream is) {
		this.is = is;
		this.sb = new StringBuilder();
	}

	@Override
	public int read() throws IOException {
		int c = is.read();
		if (c > -1) {
			sb.append((char) c);
		} else {
			this.trace();
		}
		return c;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = is.read(b);
		if (read > 0) {
			sb.append(new String(b, 0, read));
			if (read < b.length) {
				this.trace();
			}
		} else {
			this.trace();
		}
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = is.read(b, off, len);
		if (read > 0) {
			sb.append(new String(b, 0, read));
			if (read < b.length) {
				this.trace();
			}
		} else {
			this.trace();
		}
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	// TODO Check never close ?
	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		is.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		is.reset();
	}

	@Override
	public boolean markSupported() {
		return is.markSupported();
	}

	@Override
	public int hashCode() {
		return is.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof LoggableInputStream)) {
			return false;
		}
		LoggableInputStream that = (LoggableInputStream) obj;
		return this.is.equals(that.is);
	}

	private void trace() {
		log.trace("JSON Input: {}", sb.toString());
	}

}
