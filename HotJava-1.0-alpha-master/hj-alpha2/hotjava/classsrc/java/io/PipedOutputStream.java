/*
 * @(#)PipedOutputStream.java	1.2 95/01/31 James Gosling
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

package java.io;
import java.io.Piped;

/**
 * Piped output stream, must be connected to a PipedInputStream.
 * A thread reading from a PipedInputStream recieves data from
 * a thread writing to the PipedOutputStream it is connected to.
 * @see	PipedInputStream
 * @version 	95/01/31
 * @author	James Gosling
 */
public
class PipedOutputStream extends OutputStream {
    private PipedInputStream sink;

    /**
     * Creates an output file connected to a PipedInputStream
     * @param snk	The InputStream to connect to.
     */
    public PipedOutputStream(PipedInputStream snk) {
	connect(snk);
    }
    
    /**
     * Creates an output file that isn't connected to anything (yet).
     * It must be connected before being used.
     */
    public PipedOutputStream() {
    }
    
    /**
     * Connect this output stream to a reciever.
     * @param snk	The InputStream to connect to.
     */
    public void connect(PipedInputStream snk) {
	sink = snk;
	snk.closed = false;
	snk.in = -1;
	snk.out = 0;
    }

    /**
     * Write a byte. Will block until the byte is actually
     * written.
     * @exception IOException i/o error occurred
     */
    public void write(int b) {
	sink.recieve(b);
    }

    /**
     * Writes a sub array of bytes.
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException i/o error occurred
     */
    public void write(byte b[], int off, int len) {
	sink.recieve(b, off, len);
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException i/o error occurred
     */
    public void close() {
	if (sink != null) {
	    sink.recievedLast();
	}
    }
}
