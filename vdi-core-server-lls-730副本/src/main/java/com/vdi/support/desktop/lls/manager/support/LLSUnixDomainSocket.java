package com.vdi.support.desktop.lls.manager.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.etsy.net.UnixDomainSocketClient;

public class LLSUnixDomainSocket {

	public String sendMessage(String message) throws IOException {
		OutputStream out = null;
		BufferedOutputStream bufferO = null;
		UnixDomainSocketClient client =new UnixDomainSocketClient();
		try {
			out = client.getOutputStream();
			bufferO = new BufferedOutputStream(out);
			String cl = message.length() + "";
			byte[] src = message.getBytes("UTF-8");
			List<Byte> bs = new ArrayList<Byte>();
			bs.add((byte) cl.length());
			bs.add((byte) (cl.length() >> 8));
			for (Byte byte1 : cl.getBytes(Charset.forName("UTF-8"))) {
				bs.add(byte1);
			}
			for (Byte byte2 : src) {
				bs.add(byte2);
			}
			byte[] bb = new byte[bs.size()];
			for (int i = 0; i < bb.length; i++) {
				bb[i] = bs.get(i);
			}
			bufferO.write(bb);
			return client.getGuid();
		} finally {
			if (bufferO != null && out != null) {
				bufferO.close();
			}
		}
	}
	
	public String receiveMessage(UnixDomainSocketClient client)
			throws IOException {
		BufferedInputStream bfi = new BufferedInputStream(
				client.getInputStream());
		try {
			byte[] b = new byte[4096];
			StringBuffer str = new StringBuffer();
			while (bfi.read(b) != -1) {
				str.append(new String(b, Charset.forName("UTF-8")));
			}
			return str.toString();
		} finally {
			bfi.close();
			client.unlink();
		}
	}
}
