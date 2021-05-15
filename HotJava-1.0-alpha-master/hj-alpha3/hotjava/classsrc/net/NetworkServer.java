/*
 * @(#)NetworkServer.java	1.4 95/05/11 James Gosling
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */
package net;

import net.*;
import java.io.*;

/**
 * This is the base class for network servers.  To define a new type
 * of server define a new subclass of NetworkServer with a serviceRequest
 * method that services one request.  Start the server by executing:
 * <pre>
 *	new MyServerClass().startServer(port);
 * </pre>
 */
public class NetworkServer implements Runnable {

    /** Socket for communicating with client. */
    public Socket clientSocket = null;
    private Thread serverInstance;
    private boolean isServer;

    /** Stream for printing to the client. */
    public PrintStream clientOutput;

    /** Buffered stream for reading replies from client. */
    public InputStream clientInput;

    /** Close an open connection to the client. */
    public void close() {
	try {
	    clientOutput.close();
	} catch(Exception e) {
	}
	try {
	    clientInput.close();
	} catch(Exception e) {
	}
	try {
	    clientSocket.close();
	} catch(Exception e) {
	}
	clientSocket = null;
	clientInput = null;
	clientOutput = null;
    }

    /** Return client connection status */
    public boolean clientIsOpen() {
	return clientSocket != null;
    }

    final public void run() {
	if (isServer) {
	    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	    // System.out.print("Server starts " + clientSocket + "\n");
	    while (true) {
		try {
		    Socket ns = clientSocket.accept();
//		    System.out.print("New connection " + ns + "\n");
		    NetworkServer n = (NetworkServer) clone();
		    n.clientSocket = ns;
		    n.isServer = false;
		    new Thread(n).start();
		} catch(Exception e) {
		    System.out.print("Server failure\n");
		    e.printStackTrace();
		    clientSocket.close();
System.out.print("cs="+clientSocket.port+"\n");
System.out.print("ia="+clientSocket.address+"\n");
		    Socket nns = new Socket(true);
		    nns.port = clientSocket.port;
		    nns.bindToPort(clientSocket.address,
				   clientSocket.port);
		    clientSocket = nns;
		}
	    }
//	    close();
	} else {
	    try {
		clientOutput = new PrintStream(
			new BufferedOutputStream(clientSocket.outputStream),
					       false);
		clientInput = new BufferedInputStream(clientSocket.inputStream);
		serviceRequest();
		// System.out.print("Service handler exits
		// "+clientSocket+"\n");
	    } catch(Exception e) {
		// System.out.print("Service handler failure\n");
		// e.printStackTrace();
	    }
	    close();
	}
    }

    /** Start a server on port <i>port</i>.  It will call serviceRequest()
        for each new connection. */
    final public void startServer(int port) {
	Socket s = new Socket(true);
	InetAddress addr = InetAddress.getByName(InetAddress.localHostName);
	int i;
	for (i = 10; --i >= 0;) {
	    try {
		s.port = port;
		s.bindToPort(addr, port);
		break;
	    } catch(Exception e) {
		System.out.print("[Waiting to create port]\n");
		Thread.sleep(5000);
	    }
	}
	if (i < 0) {
	    System.out.print("**Failed to create port\n");
	    return;
	}
	s.listen(50);
	serverInstance = new Thread(this);
	isServer = true;
	clientSocket = s;
	serverInstance.start();
    }

    /** Service one request.  It is invoked with the clientInput and
	clientOutput streams initialized.  This method handles one client
	connection. When it is done, it can simply exit. The default
	server just echoes it's input. It is invoked in it's own private
	thread. */
    public void serviceRequest() {
	byte buf[] = new byte[300];
	int n;
	clientOutput.print("Echo server " + getClass().getName() + "\n");
	clientOutput.flush();
	while ((n = clientInput.read(buf, 0, buf.length)) >= 0) {
	    clientOutput.write(buf, 0, n);
	}
    }

    public static void main(String argv[]) {
	new NetworkServer ().startServer(8888);
    }

    public NetworkServer () {
    }
}
