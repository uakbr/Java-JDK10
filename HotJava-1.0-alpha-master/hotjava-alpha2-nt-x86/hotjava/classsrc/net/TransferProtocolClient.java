/*
 * @(#)TransferProtocolClient.java	1.17 95/02/10 Jonathan Payne
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

package net;

import java.lang.StringIndexOutOfRangeException;
import java.io.*;
import net.*;
import java.util.Vector;


/**
 * This class implements that basic intefaces of transfer protocols.
 * It is used by subclasses implementing specific protocols.
 *
 * @version 	1.17, 10 Feb 1995
 * @author 	Jonathan Payne
 * @see 	FtpClient
 * @see		NntpClient
 */

public class TransferProtocolClient extends NetworkClient {
    static final boolean debug = false;

    /** Array of strings (usually 1 entry) for the last reply
	from the server. */
    protected Vector	serverResponse = new Vector(1);

    /** code for last reply */
    protected int	lastReplyCode;


    /**
     * Pulls the response from the server and returns the code as a
     * number. Returns -1 on failure.
     */
    public int readServerResponse() {
	StringBuffer	replyBuf = new StringBuffer(32);
	int		c;
	int		continuingCode = -1;
	int		code;
	String		response;

	serverResponse.setSize(0);
	while (true) {
	    while ((c = serverInput.read()) != -1) {
		if (c == '\r') {
		    if ((c = serverInput.read()) != '\n')
			replyBuf.appendChar('\r');
		}
		replyBuf.appendChar(c);
		if (c == '\n')
		    break;
	    }
	    response = replyBuf.toString();
	    replyBuf.setLength(0);
	    if (debug) {
		System.out.print(response);
	    }
	    try {
		code = Integer.parseInt(response.substring(0, 3));
	    } catch (NumberFormatException e) {
		code = -1;
	    } catch (StringIndexOutOfRangeException e) {
		/* this line doesn't contain a response code, so
		   we just completely ignore it */
		continue;
	    }
	    serverResponse.addElement(response);
	    if (continuingCode != -1) {
		/* we've seen a XXX- sequence */
		if (code != continuingCode ||
		    (response.length() >= 4 && response.charAt(3) == '-')) {
		    continue;
		} else {
		    /* seen the end of code sequence */
		    continuingCode = -1;
		    break;
		}
	    } else if (response.length() >= 4 && response.charAt(3) == '-') {
		continuingCode = code;
		continue;
	    } else {
		break;
	    }
	}

	return lastReplyCode = code;
    }

    /** Sends command <i>cmd</i> to the server. */
    public void sendServer(String cmd) {
	serverOutput.print(cmd);
	if (debug) {
	    System.out.print("Sending: " + cmd);
	}
    }

    /** converts the server response into a string. */
    public String getResponseString() {
	return (String) serverResponse.elementAt(0);
    }

    /** Returns all server response strings. */
    public Vector getResponseStrings() {
	return serverResponse;
    }

    /** standard constructor to host <i>host</i>, port <i>port</i>. */
    public TransferProtocolClient(String host, int port) {
	super(host, port);
    }

    /** creates an uninitialized instance of this class. */
    public TransferProtocolClient() {}
}
