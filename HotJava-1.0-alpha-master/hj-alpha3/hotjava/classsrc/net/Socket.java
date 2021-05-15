/*
 * @(#)Socket.java	1.16 95/05/15 Jonathan Payne, Chuck McManis
 *
 * Copyright (c) 1994,1995 Sun Microsystems, Inc. All Rights Reserved.
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

package net;

import java.io.*;
import java.util.Linker;

/**
 * This is the basic socket class. It is currently 'SOCKSified'
 * so if you have SOCKS_HOST and SOCKS_PORT set, and the address
 * cannot be connected to locally, then we try going through sockd.
 *
 * @author	Jonathan Payne
 * @author	Chuck McManis
 * @version	1.16, 15 May 1995
 */
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

    /** set to true if the connection has gone through socks. */
    private boolean isSOCKSSocket = false;

    /**
     * SOCKS Variables. These variables are set when the user is
     * using SOCKS to go through the firewall. 
     *
     * The first SOCKSUser should be the current username.
     */
    private static String SOCKSUser;

    /** This is the host running sockd, SOCKS_HOST environment var */
    private static InetAddress SOCKSHost;

    /** Port number for sockd, by default it is 1080 */
    private static int SOCKSPort = 1080;

    /** 
     * This is the SOCKS name server 
     */
    private static InetAddress SOCKSNameServer;

    /** This is the SOCKS domain name, SOCKS_DN environment variable */
    private static String SOCKSDomainName;


    /** A pair of booleans to control SOCKS */
    private static boolean SOCKSInit;

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

    /** dup 'this' socket, this is for the fake accept that SOCKS does. */
    private native int dup();

    /** Bind this socket to a local address. */
    public native void	bindAnonymously(InetAddress addr);

    private native void	connectUnchecked(InetAddress dest, int port);

    /** Bind this socket to a known port at the specified address. */
    public native void	bindToPort(InetAddress addr, int port);

    /** Connect this socket to the specified destination and
	port.  Throws an exception if this fails. */
    public void connect(InetAddress dest, int port) {
	String	appURL = Firewall.verifyAccess(dest.hostName, port);

	if (appURL != null) {
	    String msg = "Applet at " + appURL +
		" attempted illegal socket connection to " +
		dest.hostName + ":" + port;

	    Firewall.securityError(msg);
	    return;
	}

	connectUnchecked(dest, port);
    }

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

    public Socket SOCKSAccept() {
	int	i;
	if (! usingSOCKS()) {
	    throw new SOCKSException("Not using socks on this socket.");
	}
	byte dst[] = new byte[8];

	i = inputStream.read(dst);	// Get the remote connect request.
	if (i != 8) {
	    throw new SOCKSException("remote server timed out.");
	}

	if (dst[0] != 4) {
	    throw new SOCKSException("Wrong version returned from sockd.");
	}
	if (dst[1] != 90) {
	    exceptionThrower((int) dst[1]);
	}

	Socket newSocket = new Socket();
	newSocket.address = new InetAddress();
	newSocket.address.address = ((dst[4] << 24) & 0xff000000) +
	                            ((dst[5] << 16) & 0xff0000) +
	                            ((dst[6] << 8) & 0xff00) +
	                            ((dst[7] << 0) & 0xff);
 	newSocket.port = ((dst[2] << 8) & 0xff00) + (dst[3] & 0xff);
	newSocket.s = dup();
	newSocket.makeStreams();
	return (newSocket);
    }

    private void exceptionThrower(int msgnum) {
	String msg;

	switch (msgnum) {
	    case 90: msg = new String("Call Succeeded.");
		break;
	    case 91: msg = new String("Call Failed.");
		break;
	    case 92: msg = new String("Failed to connect to identd on client.");
		break;
	    case 93: msg = new String("Identd reported different user id.");
		break;
	    default: msg = new String("SOCKS Error "+msgnum);
		break;
	}
	throw new SOCKSException(msg);
    }

    /**
     * This method returns the state of the isSOCKSSocket variable.
     * It has to be a method to prevent the variable from being overwritten
     * my malicious programs.
     */
    public boolean usingSOCKS() {
	return (isSOCKSSocket);
    }

    /**
     * Create a bound socket on the other side of a SOCKS firewall.
     */
    public void SOCKSBind(InetAddress addr, Socket remHost) {
	byte dst[];
	boolean connected;
	int	ntries;

	if (! remHost.usingSOCKS()) {
	    throw new SOCKSException("SOCKS bind called for inside host."); 
	}
	doSOCKSInit();	// Initialize SOCKS variables if not already.
	for (ntries = 0; ntries < 3; ntries++) {
	    try {
		create(true);
		makeStreams();
		connectUnchecked(Socket.SOCKSHost, Socket.SOCKSPort);
		break;
	    } catch (ProtocolException e) {
		// Sometimes connect on Solaris will get a
		// protocol exception. The only thing that seems
		// to help in that case is to close down the
		// socket and start again from scratch. 
		close();
		continue;
	    } catch (Exception e) {
		// Let someone else handle any other exception
		// after clsing the socket down. 
		close();
		throw e;
	    }
	}
	if (ntries == 3) {
	    throw new SOCKSException("Unable to connect to sockd.");
	}

	dst = new byte[8 + SOCKSUser.length() + 1]; /* connect packet */
	dst[0] = 4;	// SOCKS version
	dst[1] = 2;	// SOCKS bind command
	dst[2] = (byte) ((remHost.port >>> 8) & 0xff);
	dst[3] = (byte) (remHost.port & 0xff);
	dst[4] = (byte) ((remHost.address.address >>> 24)  & 0xff);
	dst[5] = (byte) ((remHost.address.address >>> 16)  & 0xff);
	dst[6] = (byte) ((remHost.address.address >>> 8)  & 0xff);
	dst[7] = (byte) ((remHost.address.address >>> 0)  & 0xff);
	SOCKSUser.getBytes(0, SOCKSUser.length(), dst, 8);
	dst[dst.length - 1] = 0;
	outputStream.write(dst);
	int i = inputStream.read(dst, 0, 8);
	if (i != 8) {
	    throw new SOCKSException("connection timed out.");
	}
	if (dst[1] != 90) {
	    exceptionThrower((int) dst[1]);
	}
	/* if result as INADDR_ANY */
	if ((dst[4] | dst[5] | dst[6] | dst[7]) == 0) {
	    addr.address = SOCKSHost.address;
	}
 	port = ((dst[2] << 8) & 0xff00) + (dst[3] & 0xff);
	isSOCKSSocket = true;
    }

    private void doSOCKSConnect(String host, int port) {
	byte dst[];
	InetAddress addr;
	boolean debug;

	debug = System.getenv("SOCKS_DEBUG") != null;
	if (debug)
	    System.out.println("Attempting to resolve host '"+host+"'");
	if (SOCKSNameServer == null) {
	    addr = InetAddress.getByName(host);
	} else {
	    addr = InetAddress.getByName(host, SOCKSNameServer);
   	}
	if (debug)
	    System.out.println("address is '"+addr+"'");

	/* XXX this works on UNIX but may fail on Win/Mac */
	if (SOCKSUser == null) {
	    SOCKSUser = System.getenv("USER"); 
	    if (SOCKSUser == null) {
	    	SOCKSUser = new String("NOBODY");
	    }
	}

	dst = new byte[8 + SOCKSUser.length() + 1]; /* connect packet */
	dst[0] = 4;	// SOCKS version
	dst[1] = 1;	// SOCKS connect command.
	dst[2] = (byte) ((port >>> 8) & 0xff);
	dst[3] = (byte) (port & 0xff);
	dst[4] = (byte) ((addr.address >>> 24)  & 0xff);
	dst[5] = (byte) ((addr.address >>> 16)  & 0xff);
	dst[6] = (byte) ((addr.address >>> 8)  & 0xff);
	dst[7] = (byte) ((addr.address >>> 0)  & 0xff);
	SOCKSUser.getBytes(0, SOCKSUser.length(), dst, 8);
	dst[dst.length - 1] = 0;
	outputStream.write(dst);
	int i = inputStream.read(dst, 0, 8);
	if (i != 8) {
	    throw new SOCKSException("connection timed out.");
	}
	if (dst[1] != 90) {
	    exceptionThrower((int) dst[1]);
	}
	isSOCKSSocket = true;
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


    private static void doSOCKSInit() {
	/*
	 * Initialize SOCKS variables if necessary.
	 */
	if (! SOCKSInit) {
	    String t;

	    SOCKSInit = true;
	    t = System.getenv("SOCKS_HOST");
	    if (t == null) {
		return;
	    }
	    SOCKSHost = InetAddress.getByName(t);
	    t = System.getenv("SOCKS_PORT");
	    if (t != null) {
		SOCKSPort = Integer.parseInt(t);
	    }
	    t = System.getenv("SOCKS_NS");
	    if (t != null)
	   	SOCKSNameServer = InetAddress.getByName(t);
	    SOCKSDomainName = System.getenv("SOCKS_DN");
	}
    }

    /** Create a socket which is a TCP socket if isStream is true,
	or is a UDP socket otherwise. */
    public Socket(boolean isStream) {
	create(isStream);
	makeStreams();
	doSOCKSInit();
    }

    /** Create a TCP socket and connect it to the specified port on
	the specified host. */
    public Socket(String host, int port) {
	int ntries = 3;   // number of tries before faulure.
	boolean connected = false;
	String	tmps;
	Exception	pendingException = null;
	String		appURL;

	/*
	 * Initialize SOCKS variables if necessary.
	 */
	doSOCKSInit();

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
		    connectUnchecked(InetAddress.getByName(host), port);
		    connected = true;
		} catch (ProtocolException e) {
		    // Sometimes connect on Solaris will get a
		    // protocol exception. The only thing that seems
	 	    // to help in that case is to close down the
		    // socket and start again from scratch. 
		    close();
		    pendingException = e; // save it for later
		    if (ntries == 0) {
			    break;
		    }
		} catch (UnknownHostException e) {
		    close();
		    pendingException = e; 
		    break; // Try socks if that is configured.
		} catch (Exception e) {
		    // Let someone else handle any other exception
		    // after clsing the socket down. 
		    close();
		    throw e;
		}
	    } // end while

	    /* Were we successful? If so, return from here. */
	    if (connected)
		return;

	    // If not using SOCKS really quit.
	    if (SOCKSHost == null) {
		throw pendingException;
	    }
	    /*
	     * Could not connect directly, and while statement above
	     * did a 'break' so we must be using SOCKS
	     * 
	     * XXX	Weaknesses	XXX
	     * - We aren't a "versatile" client, and don't support many
	     *   SOCKS server.
	     * - We probably should encapsulate the firewall behaviour to
	     *   support others such as NAT, etc.
	     *
	     * Step 1.  Attempt to establish a connection to the SOCKS server
	     */
	    while (! connected && ntries-- > 0) {
		try {
		    create(true);
		    makeStreams();
		    connectUnchecked(Socket.SOCKSHost, Socket.SOCKSPort);
		    connected = true;
		} catch (ProtocolException e) {
		    // Sometimes connect on Solaris will get a
		    // protocol exception. The only thing that seems
		    // to help in that case is to close down the
		    // socket and start again from scratch. 
		    close();
		    if (ntries == 0)
		        throw e;	// give up
		} catch (Exception e) {
		    // Let someone else handle any other exception
		    // after clsing the socket down. 
		    close();
		    throw e;
		}
	    }

	    /*
	     * Now we're connected to the SOCKS server, 
	     * Step 2. Tell it who we _really_ want.
	     * (throws an exception if it fails)
	     */
	    doSOCKSConnect(host, port);
	}
    }

    protected void finalize() {
	try {
	    close();
	} catch (Exception e) {
	}
    }
}
