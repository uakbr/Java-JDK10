/*
 * @(#)Socket.java	1.16 96/01/10 Jonathan Payne
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * The client Socket class. It uses a SocketImpl
 * to implement the actual socket operations. It is done this way 
 * so that you are able to change socket implementations depending 
 * on the kind of firewall that is used. You can change socket
 * implementations by setting the SocketImplFactory.
 *
 * @version     1.16, 01/10/96
 * @author 	Jonathan Payne
 * @author 	Arthur van Hoff
 */
public final 
class Socket {
    /**
     * The implementation of this Socket.
     */
    SocketImpl impl;

    /**
     * Creates an unconnected socket. Note: this method
     * should not be public.
     */
    Socket() {
	impl = (factory != null) ? factory.createSocketImpl() : new PlainSocketImpl();
    }

    /** 
     * Creates a stream socket and connects it to the specified port on
     * the specified host.
     * @param host the host
     * @param port the port
     */
    public Socket(String host, int port)
	throws UnknownHostException, IOException
    {
	this(host, port, true);
    }

    /** 
     * Creates a socket and connects it to the specified port on
     * the specified host. The last argument lets you specify whether
     * you want a stream or datagram socket.
     * @param host the specified host
     * @param port the specified port
     * @param stream a boolean indicating whether this is a stream 
     * or datagram socket
     */
    public Socket(String host, int port, boolean stream) throws IOException {
	this();

	String hostCopy = new String(host);

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkConnect(hostCopy, port);
	}

	try {
	    impl.create(stream);
	    impl.connect(hostCopy, port);
	} catch (IOException e) {
	    impl.close();
	    throw e;
	}
    }

    /** 
     * Creates a stream socket and connects it to the specified address on
     * the specified port. 
     * @param address the specified address
     * @param port the specified port
     */
    public Socket(InetAddress address, int port) throws IOException {
	this(address, port, true);
    }

    /** 
     * Creates a socket and connects it to the specified address on
     * the specified port. The last argument lets you specify whether
     * you want a stream or datagram socket.
     * @param address the specified address
     * @param port the specified port
     * @param stream a boolean indicating whether this is a stream 
     * or datagram socket
     */
    public Socket(InetAddress address, int port, boolean stream) 
	throws IOException
    {
	this();

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkConnect(address.getHostName(), port);
	}

	try {
	    impl.create(stream);
	    impl.connect(address, port);
	} catch (SocketException e) {
	    impl.close();
	    throw e;
	}
    }

    /**
     * Gets the address to which the socket is connected.
     */
    public InetAddress getInetAddress() {
	return impl.getInetAddress();
    }

    /**
     * Gets the remote port to which the socket is connected.
     */
    public int getPort() {
	return impl.getPort();
    }

    /**
     * Gets the local port to which the socket is connected.
     */
    public int getLocalPort() {
	return impl.getLocalPort();
    }

    /**
     * Gets an InputStream for this socket.
     */
    public InputStream getInputStream() throws IOException {
	return impl.getInputStream();
    }

    /**
     * Gets an OutputStream for this socket.
     */
    public OutputStream getOutputStream() throws IOException {
	return impl.getOutputStream();
    }

    /**
     * Closes the socket.
     */
    public synchronized void close() throws IOException {
	impl.close();
    }

    /**
     * Converts the Socket to a String.
     */
    public String toString() {
	return "Socket[addr=" + impl.getInetAddress() +
	    ",port=" + impl.getPort() + 
	    ",localport=" + impl.getLocalPort() + "]";
    }

    /**
     * The factory for all client sockets.
     */
    private static SocketImplFactory factory;

    /**
     * Sets the system's client SocketImplFactory. The factory can 
     * be specified only once.
     * @param fac the desired factory
     * @exception SocketException If the factory is already defined.
     */
    public static synchronized void setSocketImplFactory(SocketImplFactory fac)
	throws IOException
    {
	if (factory != null) {
	    throw new SocketException("factory already defined");
	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSetFactory();
	}
	factory = fac;
    }
}
