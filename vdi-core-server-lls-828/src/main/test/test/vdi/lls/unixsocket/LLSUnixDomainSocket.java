package test.vdi.lls.unixsocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocketClient;

public class LLSUnixDomainSocket {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		UnixDomainSocketClient client = new UnixDomainSocketClient(
				"/root/unixSockets/interfaceServer", JUDS.SOCK_STREAM);
		OutputStream out = client.getOutputStream();
		BufferedOutputStream bufferO = new BufferedOutputStream(out);
//		bufferO.
		String contentString = "{\"action\":\"reqGetDatacenter\"}";
		String cl = contentString.length() + "";
		byte[] src = contentString.getBytes("UTF-8");
		List<Byte> bs = new ArrayList<Byte>();
		bs.add((byte)cl.length());
		bs.add((byte)(cl.length()>>8));
//		bs.addAll(cl.getBytes());
		for (Byte byte1 : cl.getBytes(Charset.forName("UTF-8"))) {
			bs.add(byte1);
		}
		for (Byte byte2 : src) {
			bs.add(byte2);
		}
		byte[] bb = new byte[bs.size()];
		for (int i =0;i<bb.length;i++) {
			bb[i]=bs.get(i);
		}
		bufferO.write(bb);
		bufferO.close();
		out.close();
		BufferedInputStream bfi = new BufferedInputStream(
				client.getInputStream());
		byte[] b = new byte[4096];
		StringBuffer str = new StringBuffer();

		while (bfi.read(b) != -1) {
			str.append(new String(b, Charset.forName("UTF-8")));
		}
		System.out.println(str.toString());
		bfi.close();
		client.unlink();
	}
}
