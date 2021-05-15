/*
 * @(#)Socket.java	1.10 95/03/03 Jonathan Payne
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

import java.io.*;
import java.util.Linker;

package net;

public final class Socket {
    private int		s;		/* UNIX socket FD */

    /** address this socket is bound to */

    public InetAddress	address;

    /** Port this socket is bound to in network byte order. */
    public int 		port = -1;

    /** file for doing input on this socket */
    public FileInputStream	    inputStream;

    /** file for doing output to other end */
    public FileOutputStream	    outputStream;

    /** Link network.so into the runtime. */
    static {
	Linker.loadLibrary("net");
    }

    /** Specify how many connections can attempt to connect to this
        socket at once before getting an error. */
    public native void listen(int count);

    /** Accept a connection to this socket from some place else,
	and fill in the specified socket with the new socket
	information.  This is a helper function for the accept()
	method that returns a new Socket object. */
    private native void accept(Socket dst);

    /** This does nothing except provide an entry point for other
        related packages to cause the above static initializer to
	run. */
    public static void initialize() {}

    /** Create an internet socket.  If isStream is true, creates
	a stream socket, otherwise a DGRAM style socket is created. */
    private native void	    create(boolean isStream);

    /** Bind this socket to a local address. */
    public native void	bindAnonymously(InetAddress addr);

    /** Bind this socket to a known port at the specified address. */
    public native void	bindToPort(InetAddress addr, int port);

    /** Connect this socket to the specified destination and
	port.  Throws an exception if this fails. */
    private native void	connect(InetAddress dest, int port);

    /** Accept a connection on this socket, returning a new one.
        The old socket is still around for listening for new
	connections. */
    public Socket accept() {
	Socket	newSocket = new Socket();

	newSocket.address = new InetAddress();
	accept(newSocket);
	newSocket.makeStreams();

	return newSocket;
    }

    /** Close the connection.  Other end will get EOF on reads,
        and SIGPIPE on writes. */
    public synchronized void close() {
	/*
	 * Note that we only have to close one of the streams here
	 * because they both share the same filesdescriptor.
	 * closing one has the effect of closing filedescriptor of
	 * the other.
	 */
	inputStream.close();
	s = -1;
    }

    public String toString() {
	return "Socket[fd=" + s + ", address=" + address + ", port=" + port + "]";
    }

    /** Make the input and output streams for this socket. */
    private void makeStreams() {
	inputStream = new SocketInputStream(this, s);
	outputStream = new SocketOutputStream(this, s);
    }

    /** Create a socket object without a corresponding underlying
	system socket object associated with it.  This is used for
	the accept() call, which in UNIX, anyway, allocates a new
	system-level socket upon return (which we then use to fill
	in this socket with). */
    private Socket() {
    }

    /** Create a socket which is a TCP socket if isStream is true,
	or is a UDP socket otherwise. */
    public Socket(boolean isStream) {
	create(isStream);

	makeStreams();
    }

    /** Create a TCP socket and connect it to the specified port on
	the specified host. */
    public Socket(String host, int port) {
	int ntries = 3;   // number of tries before faulure.
	boolean connected = false;
	String		appURL = Firewall.verifyAccess(host, port);

	if ((appURL = Firewall.verifyAccess(host, port)) != null) {
	    String msg = "Applet at " + appURL +
		" attempted illegal socket connection to " +
		host + ":" + port;

	    Firewall.securityError(msg);
	} else {
	    while (!connected && ntries-- > 0) {
		try {
		    create(true);
		    makeStreams();
		    connect(InetAddress.getByName(host), port);
		    connected = true;
		} catch (ProtocolException e) {
		    // Sometimes connect on Solaris will get a
		    // protocol exception. The only thing that seems
		    // to help in that case is to close down the
		    // socket and start again from scratch. 
		    close();
		    if (ntries == 0)
			throw e;
		    continue;
		} catch (Exception e) {
		    // Let someone else handle any other exception
		    // after clsing the socket down. 
		    close();
		    throw e;
		}
	    } // end while
	}
    }

    protected void finalize() {
	try {
	    close();
	} catch (Exception e) {
	}
    }
}
