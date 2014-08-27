package com.opzoon.vdi.core.util;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggableOutputStream extends OutputStream {
	
	private static final Logger log = LoggerFactory.getLogger(LoggableInputStream.class);
	
	private final OutputStream os;
	
	private final StringBuilder sb;

	public LoggableOutputStream(OutputStream os) {
		this.os = os;
		this.sb = new StringBuilder();
	}

	@Override
	public void write(int b) throws IOException {
		sb.append((char) b);
		os.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		sb.append(new String(b));
		os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		sb.append(new String(b, off, len));
		os.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		this.trace();
		os.flush();
	}

	@Override
	public void close() throws IOException {
		this.trace();
		os.close();
	}

	@Override
	public int hashCode() {
		return os.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof LoggableOutputStream)) {
			return false;
		}
		LoggableOutputStream that = (LoggableOutputStream) obj;
		return this.os.equals(that.os);
	}

	private void trace() {
		log.trace("JSON Output: {}", sb.toString());
	}

}
