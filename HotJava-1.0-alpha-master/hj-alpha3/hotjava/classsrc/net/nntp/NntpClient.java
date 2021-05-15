/*
 * @(#)NntpClient.java	1.12 95/05/10 Jonathan Payne, James Gosling
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

package net.nntp;

import java.io.*;
import net.*;
import java.util.*;

/**
 * This class implements network news clients (NNTP).
 *
 * @version	1.9, 12 Dec 1994
 * @author	Jonathan Payne, James Gosling
 */
public class NntpClient extends TransferProtocolClient {
    String serverName;		/* for re-opening server connections */
    int serverPort;

    public NntpClient () {
    }

    /** Create new NNTP Client connected to host <i>host</i> */
    public NntpClient (String host) {
	super();
	openServer(host, InetAddress.getPortByName("nntp"));
    }

    private void assert(boolean expr) {
	if (!expr)
	    throw new Exception("assertion failed");
    }

    /**
     * Open a connection to the NNTP server.
     * @exception NntpProtocolException did not get the correct welcome message
     */
    public void openServer(String name, int port) {
	serverName = name;
	serverPort = port;
	super.openServer(name, port);
	if (readServerResponse() >= 300)
	    throw new NntpProtocolException("Welcome message");
    }

    /** Sends command <i>cmd</i> to the server. */
    public int askServer(String cmd) {
	int code = 503;
	for (int tries = 3; --tries >= 0;) {
	    try {
		serverOutput.print(cmd);
		code = readServerResponse();
		if (code < 500)
		    return code;

		/*
		 * errors codes >500 usually result from something happening
		 * on the net.  Its usually profitable to disconnect and
		 * reconnect
		 */
	    } catch(Exception e) {
	    }
	    /* reconnect to the server */
	    try {
		serverOutput.close();
	    } catch(Exception e2) {
	    }
	    openServer(serverName, serverPort);
	}
	return code;
    }

    InputStream makeStreamRequest(String cmd, int reply) {
	int response;

	response = askServer(cmd + "\r\n");
	if (response != reply) {
	    String msg = null;
	    try {
		for (int i = 0; i < 99; i++) {
		    String n = (String) serverResponse.elementAt(i);
		    if (msg == null)
			msg = n;
		    else
			msg = msg + "\n" + n;
		}
	    } catch(Exception e) {
	    };
	    if (msg == null)
		msg = "Command " + cmd + " yielded " + response + "; expecting " + reply;
	    throw new NntpProtocolException(msg);
	}
	switch (response / 100) {
	  case 1:
	  case 2:
	    break;

	  case 3:
	    throw new NntpProtocolException("More input to command expected");

	  case 4:
	    throw new NntpProtocolException("Server error - cmd OK");

	  case 5:
	    throw new NntpProtocolException("Error in command: " + cmd);
	}
	return new NntpInputStream(new TelnetInputStream(serverInput, false));
    }
    String tokenize(String input)[] {
	Vector v = new Vector();
	StringTokenizer t = new StringTokenizer(input);
	String cmd[];

	while (t.hasMoreTokens())
	    v.addElement(t.nextToken());
	cmd = new String[v.size()];
	for (int i = 0; i < cmd.length; i++)
	    cmd[i] = (String) v.elementAt(i);

	return cmd;
    }

    /**
     * Get information about group <i>name</i>.
     * @exception UnknownNewsgroupException the group name wasn't active.
     * @exception NntpProtocolException received an unexpected reply.
     */
    public NewsgroupInfo getGroup(String name) {
	switch (askServer("group " + name + "\r\n")) {
	  case 411:
	    throw new UnknownNewsgroupException(name);

	  default:
	    throw new NntpProtocolException("unexpected reply: "
					    + getResponseString());

	  case 211:
	    {
		String tokens[] = tokenize(getResponseString());
		int start;
		int end;

		assert(tokens[0].equals("211"));
		assert(tokens.length >= 5);
		start = Integer.parseInt(tokens[2]);
		end = Integer.parseInt(tokens[3]);
		assert(tokens[4].equals(name));
		return new NewsgroupInfo(name, start, end);
	    }
	}
    }

    /** Set the current group to <i>name</i> */
    public void setGroup(String name) {
	if (askServer("group " + name + "\r\n") != 211)
	    throw new UnknownNewsgroupException(name);
    }

    /** get article <i>n</i> from the current group. */
    public InputStream getArticle(int n) {
	return makeStreamRequest("article " + n, 220);
    }

    /** get article <i>id</i> from the current group. */
    public InputStream getArticle(String id) {
	if (id.charAt(0) != '<')
	    id = "<" + id + ">";
	return makeStreamRequest("article " + id, 220);
    }

    /** get header of article <i>n</i> from the current group. */
    public InputStream getHeader(int n) {
	return makeStreamRequest("head " + n, 221);
    }
    /** get header of article <i>id</i> from the current group. */
    public InputStream getHeader(String id) {
	if (id.charAt(0) != '<')
	    id = "<" + id + ">";
	return makeStreamRequest("head " + id, 221);
    }
    /** Setup to post a message.  It returns a stream
        to which the article should be written.  Returns null if the post
	is disallowed.  The article must have a properly formed RFC850 header
	and end-of-lines must by sent as \r\n.  The Article must end with
	\r\n */
    public PrintStream startPost() {
	return askServer("post\r\n") == 340 ? serverOutput : null;
    }
    /** Finish posting a message.  Must be called after calling startPost
	and writing the article.  Returns true if the article is posted
	successfully. */
    public boolean finishPost() {
	return askServer(".\r\n") == 240;
    }
}
