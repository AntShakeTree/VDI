package com.vdi.support.desktop.lls.manager.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vdi.common.Constants;
import com.vdi.common.ParseJSON;
import com.vdi.common.StringUtils;
import com.vdi.support.desktop.lls.domain.BasicDomain;
import com.vdi.support.desktop.lls.domain.Head;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.manager.exception.LLSRuntimeException;

@Component("llsHandle")
public class LLSLocalNIOHandle implements LLSConnection, LLSSendMessage {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(LLSLocalNIOHandle.class);
	private volatile SocketChannel socketChannel;
	private static Selector SELECTOR;// 定义一个记录套接字通道事件的对象
	private static volatile boolean connect = false;
	private String ip = "";
	private int port;

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public LLSLocalNIOHandle() {
		try {
			SELECTOR = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	@Override
	public <T extends BasicDomain> T sendMessage(Object obj, Class<T> clazz) {

		LOGGER.debug("sendMessage :{}", obj);
		try {
			reconnection();
			String message = ParseJSON.toJson(obj);
			sendbuffer.clear();
			int len = message.length();
			// sendbuffer.put((byte) len.length());
			// sendbuffer.put((byte) (len.length() >> 8));
			// sendbuffer.put(len.getBytes("UTF-8"));
			sendbuffer.putInt(len);
			sendbuffer.put(message.getBytes("UTF-8"));
			sendbuffer.flip();
			socketChannel.write(sendbuffer);

			return receiveMessage(obj, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			this.close();
		}

		return null;
	}

	@Override
	public synchronized void connection(String ip, int port) {
		if (StringUtils.isEmpty(ip)) {
			this.ip = Constants.LLS_SORCKET_ADDRESS;
			this.port = Constants.LLS_SORCKET_PORT;
		}
		try {
			SocketAddress address = new InetSocketAddress(this.ip, this.port);
			socketChannel = SocketChannel.open();

			socketChannel.connect(address);

			while (!socketChannel.finishConnect()) {
				Thread.sleep(1);
			}
			connect = socketChannel.isOpen();
			socketChannel.configureBlocking(false);
			if (!SELECTOR.isOpen()) {
				SELECTOR = Selector.open();
			}

			socketChannel.register(SELECTOR, SelectionKey.OP_READ);
		} catch (Exception e) {
			this.close();
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			if (SELECTOR != null && this.socketChannel != null) {
				socketChannel.close();
				SELECTOR.close();
				connect = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void reconnection() {

		if (this.socketChannel == null) {
			this.connection(ip, port);
		} else {
			long start = System.currentTimeMillis();
			while (!connect) {
				this.connection(ip, port);
				long end = System.currentTimeMillis();
				if ((end - start) / 1000 == 180) {
					throw new LLSRuntimeException("time out");
				}
			}
		}
	}

	@Override
	public boolean isConnection() {
		return this.socketChannel.socket().isClosed();
	}

	private synchronized <T extends BasicDomain> T receiveMessage(Object obj,
			Class<T> clazz) throws IOException {
		while (true) {
			if (!socketChannel.isOpen()) {
				throw new LLSRuntimeException("{connection Exception}");
			}
			if (SELECTOR.select() == 0) {
				continue;
			}
			for (SelectionKey key : SELECTOR.selectedKeys()) {

				if (key.isReadable()) {
					return this.readDataFromSocket(key, obj, clazz);
				}
			}
		}
	}

	protected <T extends BasicDomain> T readDataFromSocket(SelectionKey key,
			Object obj, Class<T> typeReference) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		T t = null;
		try {
			int count = -1;
			StringBuilder sb = new StringBuilder("");
			int len = 0;
			intBuffer.clear();
			socketChannel.read(intBuffer);
			intBuffer.flip();
			len = intBuffer.getInt();
			System.out.println(len);
			intBuffer.clear();
			int c = 0;
			while (c < len) {
				readBuffer.clear();
				count = socketChannel.read(readBuffer);
				if (count > 0) {
					readBuffer.flip();
					byte[] bs = new byte[count];
					readBuffer.get(bs);
					String result = new String(bs, "UTF-8");
					sb.append(result);
					c += count;
				}
			}
			String endResult = sb.toString();
			endResult = endResult.substring(endResult.indexOf("{"));
			t = ParseJSON.fromJson(endResult, typeReference);
			LOGGER.debug("receive :{}", endResult);
			//
			if (!(t.getResult().equalsIgnoreCase("successed"))) {
				throw new LLSRuntimeException(ParseJSON.toJson(new Head()
						.setError(t.getErrorCode())));
			}
			//
			if (count == -1) {
				close();
				reconnection();
				sendMessage(obj, typeReference);
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			this.close();
		} finally {
			SELECTOR.selectedKeys().remove(key);
		}
		return null;
	}

	ByteBuffer sendbuffer = ByteBuffer.allocateDirect(4096);
	ByteBuffer readBuffer = ByteBuffer.allocateDirect(4096);
	ByteBuffer intBuffer = ByteBuffer.allocate(4);
}