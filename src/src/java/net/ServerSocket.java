/*
 * @(#)ServerSocket.java	1.18 95/12/18 Jonathan Payne
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
import java.io.FileDescriptor;

/**
 * The server Socket class. It uses a SocketImpl
 * to implement the actual socket operations. It is done this way 
 * so that you are able to change socket implementations depending 
 * on the kind of firewall being used. You can change socket
 * implementations by setting the SocketImplFactory.
 *
 * @version     1.18, 12/18/95
 * @author 	Jonathan Payne
 * @author 	Arthur van Hoff
 */
public final 
class ServerSocket {
    /**
     * The implementation of this Socket.
     */
    SocketImpl impl;

    /**
     * Creates an unconnected server socket. Note: this method
     * should not be public.
     * @exception IOException IO error when opening the socket.
     */
    ServerSocket() throws IOException {
	impl = (factory != null) ? factory.createSocketImpl() : new PlainSocketImpl();
    }

    /**
     * Creates a server socket on a specified port.
     * @param port the port
     * @exception IOException IO error when opening the socket.
     */
    public ServerSocket(int port) throws IOException {
	this(port, 50);
    }

    /**
     * Creates a server socket, binds it to the specified local port 
     * and listens to it.  You can connect to an annonymous port by 
     * specifying the port number to be 0.
     * @param port the specified port
     * @param count the amountt of time to listen for a connection
     */
    public ServerSocket(int port, int count) throws IOException {
	this();

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkListen(port);
	}

	impl.create(true);
	impl.bind(InetAddress.anyLocalAddress, port);
	impl.listen(count);
    }

    /**
     * Gets the address to which the socket is connected.
     */
    public InetAddress getInetAddress() {
	return impl.getInetAddress();
    }

    /**
     * Gets the port on which the socket is listening.
     */
    public int getLocalPort() {
	return impl.getLocalPort();
    }

    /**
     * Accepts a connection. This method will block until the
     * connection is made.
     * @exception IOException IO error when waiting for the connection.
     */
    public Socket accept() throws IOException {
	Socket s = new Socket();

	try {
	    //s.impl.create(true);
	    s.impl.address = new InetAddress();
	    s.impl.fd = new FileDescriptor();
	    impl.accept(s.impl);

	    SecurityManager security = System.getSecurityManager();
	    if (security != null) {
		security.checkAccept(s.getInetAddress().getHostName(),
				     s.getPort());
	    }
	} catch (IOException e) {
	    s.close();
	    throw e;
	} catch (SecurityException e) {
	    s.close();
	    throw e;
	}
	
	return s;
    }

    /**
     * Closes the server socket.
     * @exception IOException IO error when closing the socket.
     */
    public void close() throws IOException {
	impl.close();
    }

    /**
     * Returns the implementation address and implementation port of 
     * this ServerSocket as a String.
     */
    public String toString() {
	return "ServerSocket[addr=" + impl.getInetAddress() + 
		",port=" + impl.getPort() + 
		",localport=" + impl.getLocalPort()  + "]";
    }

    /**
     * The factory for all server sockets.
     */
    private static SocketImplFactory factory;

    /**
     * Sets the system's server SocketImplFactory. The factory can 
     * be specified only once.
     * @param fac the desired factory
     * @exception SocketException If the factory has already been 
     * defined.
     * @exception IOException IO error when setting the socket factor.
     */
    public static synchronized void setSocketFactory(SocketImplFactory fac) throws IOException {
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
