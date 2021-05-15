/*
 * @(#)DatagramSocket.java	1.9 96/01/11 Pavani Diwanji
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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * The datagram socket class implements unreliable datagrams.
 * @author Pavani Diwanji
*/
public
class DatagramSocket {
    private int localPort;
    private FileDescriptor fd;

    /**
     * Load net library into runtime.
     */
    static {
	System.loadLibrary("net");
    }

    /**
     * Creates a datagram socket
     */
    public DatagramSocket() throws SocketException {
        fd = new FileDescriptor();
	// creates a udp socket
	datagramSocketCreate();
	// binds the udp socket to any local available port
	localPort = datagramSocketBind(0);
    }

    /**
     * Creates a datagram socket
     * @param local port to use
     */
    public DatagramSocket(int port) throws SocketException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkListen(port);
	}
        fd = new FileDescriptor();
	// creates a udp socket
	datagramSocketCreate();
	// binds the udp socket to desired port
	localPort = datagramSocketBind(port);
    }

    /**
     * Sends Datagram Packet to the destination address
     * @param DatagramPacket to be sent. The packet contains the buffer 
     * of bytes, length and destination InetAddress and port.
     * @exception IOException i/o error occurred
     */
    public void send(DatagramPacket p) throws IOException  {

	// check the address is ok wiht the security manager on every send.
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkConnect(p.getAddress().getHostName(), p.getPort());
	}

	// call the native method to send
	datagramSocketSend(p);
    }

    /**
     * Receives datagram packet.
     * @param DatagramPacket to be received.
     * On return, the DatagramPacket contains the buffer in which the 
     * data is received, packet length, sender's address and sender's port 
     * number. Blocks until some input is available.
     * @exception IOException i/o error occurred
     */
    
    public synchronized void receive(DatagramPacket p) throws IOException {
        InetAddress peekAddress = new InetAddress();
	
        // peek at the packet to see who it is from.
        int peekPort = datagramSocketPeek(peekAddress);

	// check the address is ok with the security manager before every recv.
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	   security.checkConnect(peekAddress.getHostName(), peekPort);
	}
	
        // If the security check succeeds, then receive the packet.
	datagramSocketReceive(p);

	return;
    }

    /**
     *	Returns the local port that this socket is bound to.
     */
    public int getLocalPort() {
	return localPort;
    }

    /**
     * Close the datagram socket.
     */
    public synchronized void close() {
	datagramSocketClose();
    }

    protected synchronized void finalize() {
	datagramSocketClose();
    }

    private native void datagramSocketCreate();
    private native int  datagramSocketBind(int port);
    private native void datagramSocketSend(DatagramPacket p);
    private native int datagramSocketPeek(InetAddress i);
    private native void datagramSocketReceive(DatagramPacket p);
    private native void datagramSocketClose();
}
