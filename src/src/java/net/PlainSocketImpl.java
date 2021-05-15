/*
 * @(#)PlainSocketImpl.java	1.16 95/12/01 Jonathan Payne
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InterruptedIOException;
import java.io.FileDescriptor;

/**
 * Default Socket Implementation. This implementation does
 * not implement any security checks neither does it support
 * any fire walls. Note this class should <b>NOT</b> be public.
 *
 * @version     1.16, 12/01/95
 * @author 	Jonathan Payne
 * @author 	Arthur van Hoff
 */
class PlainSocketImpl extends SocketImpl
{
    /**
     * Load net library into runtime.
     */
    static {
	System.loadLibrary("net");
    }

    /**
     * Creates a socket with a boolean that specifies whether this
     * is a stream socket or a datagram socket.
     * @param stream the stream to be created
     */
    protected synchronized void create(boolean stream) throws IOException {
	fd = new FileDescriptor();
	socketCreate(stream);
    }

    /** 
     * Creates a socket and connects it to the specified port on
     * the specified host.
     * @param host the specified host
     * @param port the specified port 
     */
    protected void connect(String host, int port)
        throws UnknownHostException, IOException
    {
	IOException pending = null;
	try {
	    InetAddress address = InetAddress.getByName(host);

	    for (int i = 0 ; i < 3 ; i++) {
		try {
		    socketConnect(address, port);
		    return;
		} catch (ProtocolException e) {
		    // Try again in case of a protocol exception
		    socketClose();
	 	    fd = new FileDescriptor();
		    socketCreate(true);
		    pending = e;
		} catch (IOException e) {
		    // Let someone else deal with this exception
		    socketClose();
		    throw e;
		}
	    }
	} catch (UnknownHostException e) {
	    pending = e;
	}

	// everything failed
	socketClose();
	throw pending;
    }

    /** 
     * Creates a socket and connects it to the specified address on
     * the specified port.
     * @param address the address
     * @param port the specified port
     */
    protected void connect(InetAddress address, int port) throws IOException {
	this.port = port;
	this.address = address;

	ProtocolException pending = null;
	for (int i = 0 ; i < 3 ; i++) {
	    try {
		socketConnect(address, port);
		return;
	    } catch (ProtocolException e) {
		// Try again in case of a protocol exception
		socketClose();
		fd = new FileDescriptor();
		socketCreate(true);
		pending = e;
	    } catch (IOException e) {
		// Let someone else deal with this exception
		socketClose();
		throw e;
	    }
	}
	// everything failed
	socketClose();
	throw pending;
    }

    /**
     * Binds the socket to the specified address of the specified local port.
     * @param address the address
     * @param port the port
     */
    protected synchronized void bind(InetAddress address, int lport) 
	throws IOException
    {
	socketBind(address, lport);
    }

    /**
     * Listens, for a specified amount of time, for connections.
     * @param count the amount of time to listen for connections
     */
    protected synchronized void listen(int count) throws IOException {
	socketListen(count);
    }

    /**
     * Accepts connections.
     * @param s the connection
     */
    protected synchronized void accept(SocketImpl s) throws IOException {
	socketAccept(s);
    }

    /**
     * Gets an InputStream for this socket.
     */
    protected synchronized InputStream getInputStream() throws IOException {
	return new SocketInputStream(this);
    }

    /**
     * Gets an OutputStream for this socket.
     */
    protected synchronized OutputStream getOutputStream() throws IOException {
	return new SocketOutputStream(this);
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     */
    protected synchronized int available() throws IOException {
	return socketAvailable();
    }

    /**
     * Closes the socket.
     */
    protected synchronized void close() throws IOException {
	if (fd != null) socketClose();
    }

    /**
     * Cleans up if the user forgets to close it.
     */
    protected synchronized void finalize() throws IOException {
	if (fd != null) socketClose();
    }

    private native void socketCreate(boolean stream) throws IOException;
    private native void socketConnect(InetAddress address, int port)
	throws IOException;
    private native void socketBind(InetAddress address, int port)
	throws IOException;
    private native void socketListen(int count)
	throws IOException;
    private native void socketAccept(SocketImpl s)
	throws IOException;
    private native int socketAvailable()
	throws IOException;
    private native void socketClose()
	throws IOException;
}
