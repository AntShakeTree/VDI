package com.etsy.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.vdi.common.Constants;

/*
 * maxiaochao
 */
public class UnixDomainSocketClient extends UnixDomainSocket {
	private  String  guid;
	
	/**
	 * Creates a Unix domain socket and connects it to the server specified by
	 * the socket file.
	 * 
	 * @param socketFile
	 *            Name of the socket file
	 * 
	 * @exception IOException
	 *                If unable to construct the socket
	 */

	public UnixDomainSocketClient(String socketFile, int socketType)
			throws IOException {
		super.socketFile = socketFile;
		super.socketType = socketType;

		if ((nativeSocketFileHandle = nativeOpen(socketFile, socketType)) == -1)
			throw new IOException("Unable to open Unix domain socket");

		// Initialize the socket input and output streams
		if (socketType == JUDS.SOCK_STREAM)
			in = new UnixDomainSocketInputStream();
		out = new UnixDomainSocketOutputStream();
	}

	/**
	 * Returns an input stream for this socket.
	 * 
	 * @exception UnsupportedOperationException
	 *                if <code>getInputStream</code> is invoked for an
	 *                <code>UnixDomainSocketClient</code> of type
	 *                <code>JUDS.SOCK_DGRAM</code>.
	 * @return An input stream for writing bytes to this socket
	 */
	@Override
	public InputStream getInputStream() {
		if (socketType == JUDS.SOCK_STREAM)
			return (InputStream) in;
		else
			throw new UnsupportedOperationException();
	}

	public  UnixDomainSocketClient ()
			throws IOException {
		guid=UUID.randomUUID().toString();
		new UnixDomainSocketClient(
				Constants.LLS_UNIX_SORCKET_FILE, JUDS.SOCK_STREAM);
	}

	public String getGuid() {
		return guid;
	}

	
	
}
