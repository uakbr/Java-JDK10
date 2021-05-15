/*
 * @(#)SocketInputStream.java	1.13 95/05/10 Jonathan Payne
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

import java.io.*;
import java.util.*;

/**
 * This stream extends FileInputStream to implement BSD style UDP sockets.
 * It closely parallels the BSD sockets interface.
 *
 * @version	1.13, 10 May 1995
 * @author	Jonathan Payne
 */
class SocketInputStream extends FileInputStream {
    private boolean eof = false;
    private Socket owner;

    public SocketInputStream(Socket owner, int sock) {
	super(sock);
	this.owner = owner;
    }

    /** Read into an array of bytes at the specified offset using
        the recv socket primitive. */
    private native int recv(byte b[], int off, int len);

    /** Read into a byte array data from the socket. */
    public int read(byte b[]) {
	return read(b, 0, b.length);
    }


    /** 
     * Read into a byte array <i>b</i> at offset <i>off</i>, <i>length</i>
     * bytes of data.
     */
    public int read(byte b[], int off, int length) {
	if (eof) {
	    return -1;
	}
	int n = recv(b, off, length);
	if (n <= 0) {
	    eof = true;
	    return -1;
	}
	return n;
    }

    byte temp[] = new byte[1];

    /** Read a single byte from the socket. */
    public int read() {
	if (eof) {
	    return -1;
	}

 	int n = read(temp, 0, 1);
	if (n <= 0) {
	    return -1;
	}
	return temp[0] & 0xff;
    }

    /** not implemented for sockets, always returns zero. */
    public int skip(int n) {
	return(0);
    }

    /** not implemented for sockets, always returns zero. */
    public int available() {
	return(0);
    }

    /** override finalize, the fd is closed by the Socket */
    protected void finalize() {
    }
}

