/*
 * @(#)PipedInputStream.java	1.10 95/08/16 James Gosling
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

package java.io;

import java.io.*;
import java.io.IOException;

/**
 * PipedInputStream must be connected to a PipedOutputStream
 * to be useful.  A thread reading from a PipedInputStream recieves data from
 * a thread writing to the PipedOutputStream it is connected to.
 *
 * @see	PipedOutputStream
 * @version 	95/08/16
 * @author	James Gosling
 */
public
class PipedInputStream extends InputStream {
    boolean closed = true;	

	/* REMIND: identification of the read and write sides needs to be
	   more sophisticated.  Either using thread groups (but what about
	   pipes within a thread?) or using finalization (but it may be a
	   long time until the next GC). */
    Thread readSide;
    Thread writeSide;

    /* The circular buffer into which incoming data is placed */
    private byte buffer[] = new byte[1024];

    /*
     * fill and empty pointers.  in<0 implies the buffer is empty, in==out
     * implies the buffer is full
     */
    int in = -1;
    int out = 0;

    /**
     * Creates an input file from the specified PiledOutputStream.
     * @param src the stream to connect to.
     */
    public PipedInputStream (PipedOutputStream src) throws IOException {
	connect(src);
    }

    /**
     * Creates an input file that isn't connected to anything (yet).
     * It must be connected to a PipedOutputStream before being used.
     */
    public PipedInputStream () {
    }

    /**
     * Connects this input stream to a sender.
     * @param src	The OutputStream to connect to.
     */
    public void connect(PipedOutputStream src) throws IOException {
	src.connect(this);
    }
    
    /**
     * Receives a byte of data.  This method will block if no input is
     * available.
     * @param b the byte being received
     * @exception IOException If the pipe is broken.
     */
    synchronized void receive(int b) throws IOException {
	writeSide = Thread.currentThread();
	while (in == out) {
	    if ((readSide != null) && !readSide.isAlive()) {
		throw new IOException("Pipe broken");
	    }
	    /* full: kick any waiting readers */
	    notifyAll();	
	    try {
	        wait(1000);
	    } catch (InterruptedException ex) {
		throw new java.io.InterruptedIOException();
	    }
	}
	if (in < 0) {
	    in = 0;
	    out = 0;
	}
	buffer[in++] = (byte)(b & 0xFF);
	if (in >= buffer.length) {
	    in = 0;
	}
    }

    /**
     * Receives data into an array of bytes.  This method will
     * block until some input is available. 
     * @param b the buffer into which the data is received
     * @param off the start offset of the data
     * @param len the maximum number of bytes received
     * @return the actual number of bytes received, -1 is
     *          returned when the end of the stream is reached. 
     * @exception IOException If an I/O error has occurred. 
     */
    synchronized void receive(byte b[], int off, int len)  throws IOException {
	while (--len >= 0) {
	    receive(b[off++]);
	}
    }

    /**
     * Notifies all waiting threads that the last byte of data has been
     * received.
     */
    synchronized void receivedLast() {
	closed = true;
	notifyAll();
    }

    /**
     * Reads a byte of data. This method will block if no input is available.
     * @return 	the byte read, or -1 if the end of the stream is reached.
     * @exception IOException If the pipe is broken.
     */
    public synchronized int read()  throws IOException {
	int trials = 2;
	while (in < 0) {
	    readSide = Thread.currentThread();
	    if ((writeSide != null) && (!writeSide.isAlive()) && (--trials < 0)) {
		throw new IOException("Pipe broken");
	    }
	    if (closed) {
		return -1;
	    }

            /* might be a writer waiting */
	    notifyAll();
	    try {
	        wait(1000);
	    } catch (InterruptedException ex) {
		throw new java.io.InterruptedIOException();
	    }
 	}
	int ret = buffer[out++] & 0xFF;
	if (out >= buffer.length) {
	    out = 0;
	}
	if (in == out) {
            /* now empty */
	    in = -1;		
	}
	return ret;
    }

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public synchronized int read(byte b[], int off, int len)  throws IOException {
	if (len <= 0) {
	    return 0;
	}

        /* possibly wait on the first character */
	int c = read();		
	if (c < 0) {
	    return -1;
	}
	b[off] = (byte) c;
	int rlen = 1;
	while ((in >= 0) && (--len > 0)) {
	    b[off + rlen] = buffer[out++];
	    rlen++;
	    if (out >= buffer.length) {
		out = 0;
	    }
	    if (in == out) {
                /* now empty */
		in = -1;	
	    }
	}
	return rlen;
    }

    /**
     * Closes the input stream. Must be called
     * to release any resources associated with
     * the stream.
     * @exception IOException If an I/O error has occurred.
     */
    public void close()  throws IOException {
	in = -1;
	closed = true;
    }

}
