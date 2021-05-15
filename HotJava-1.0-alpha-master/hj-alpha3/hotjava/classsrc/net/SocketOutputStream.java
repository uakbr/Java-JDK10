/*
 * @(#)SocketOutputStream.java	1.10 95/05/10 Jonathan Payne
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

import  java.io.*;
import  java.util.*;

/**
 * This class implements the output (write) side of BSD style UDP sockets.
 * Other protocols such as TCP, ICMP, ARP, etc are not supported at this
 * time. 
 * @version 1.10, 10 May 1995
 * @author Jonathan Payne
 */
class SocketOutputStream extends FileOutputStream {
    private Socket owner;
    
    public SocketOutputStream(Socket owner, int sock) {
	super(sock);
	this.owner = owner;
    }

    byte temp[] = new byte[1];

    /** write  a byte to the socket. */
    public void write(int b) {
	temp[0] = (byte) b;
	write(temp, 0, 1);
    }

    /** 
     * Write contents of buffer <i>b</i> to the socket.
     */
    public void write(byte b[]) {
	write(b, 0, b.length);
    }

    /** 
     * Write <i>length</i> bytes from buffer <i>b</i> starting at offset 
     * <i>len</i>
     */
    public native void write(byte b[], int off, int len);

    /** override finalize, the fd is closed by the Socket */
    protected void finalize() {
    }
}

