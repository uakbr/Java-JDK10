/*
 * @(#)SocketImpl.java	1.16 95/12/18 Jonathan Payne
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
import java.io.FileDescriptor;

/**
 * This is the Socket implementation class. It is an
 * abstract class that must be subclassed to provide
 * an actual implementation.
 *
 * @version     1.16, 12/18/95
 * @author 	Jonathan Payne
 * @author 	Arthur van Hoff
 */
public abstract class SocketImpl {

    /**
     * The file descriptor object
     */
    protected FileDescriptor fd;
    
    /**
     * The internet address where the socket will make a connection.
     */
    protected InetAddress address;
   
    /**
     * The port where the socket will make a connection.
     */
    protected int port;
    protected int localport;   

    /**
     * Creates a socket with a boolean that specifies whether this
     * is a stream socket or a datagram socket.
     * @param stream a boolean indicating whether this is a stream
     * or datagram socket
     */
    protected abstract void create(boolean stream) throws IOException;

    /**
     * Connects the socket to the specified port on the specified host.
     * @param host the specified host of the connection
     * @param port the port where the connection is made
     */
    protected abstract void connect(String host, int port) throws IOException;

    /**
     * Connects the socket to the specified address on the specified
     * port.
     * @param address the specified address of the connection
     * @param port the specified port where connection is made
     */
    protected abstract void connect(InetAddress address, int port) throws IOException;

    /**
     * Binds the socket to the specified port on the specified host.
     * @param host the host
     * @param port the port   
     */
    protected abstract void bind(InetAddress host, int port) throws IOException;

    /**
     * Listens for connections over a specified amount of time.
     * @param count the amount of time this socket will listen for 
     * connections
     */
    protected abstract void listen(int count) throws IOException;

    /**
     * Accepts a connection.
     * @param s the accepted connection
     */
    protected abstract void accept(SocketImpl s) throws IOException;

    /**
     * Gets an InputStream for this socket.
     */
    protected abstract InputStream getInputStream() throws IOException;

    /**
     * Gets an OutputStream for this socket.
     */
    protected abstract OutputStream getOutputStream() throws IOException;

    /**
     * Returns the number of bytes that can be read without blocking.
     */
    protected abstract int available() throws IOException;

    /**
     * Closes the socket.
     */
    protected abstract void close() throws IOException;

    protected FileDescriptor getFileDescriptor() {
	return fd;
    }
    protected InetAddress getInetAddress() {
	return address;
    }
    protected int getPort() {
	return port;
    }
    protected int getLocalPort() {
	return localport;
    }
    
    /**
     * Returns the address and port of this Socket as a String.
     */
    public String toString() {
	return "Socket[addr=" + getInetAddress() +
	    ",port=" + getPort() + ",localport=" + getLocalPort()  + "]";
    }
}
