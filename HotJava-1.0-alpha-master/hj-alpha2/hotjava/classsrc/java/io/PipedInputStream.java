/*
 * @(#)PipedInputStream.java	1.4 95/02/27 James Gosling
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

/**
 * Piped input stream, must be connected to a PipedOutputStream
 * to be useful.
 * @see	PipedOutputStream
 * @see	Piped
 * @version 	95/02/27
 * @author	James Gosling
 */
public
class PipedInputStream extends InputStream {
    boolean closed = true;	/* true iff this end or the other end has
				 * been closed */
    Thread readSide, writeSide;	/* The two ends of the pipe */

    /**
     * Creates an input file given a PiledOutputStream.
     * @param stream	the stream to connect to.
     */
    public PipedInputStream (PipedOutputStream src) {
	connect(src);
    }

    /**
     * Creates an input file that isn't connected to anything (yet).
     * It must be connected to a PipedOutputStream before being used.
     */
    public PipedInputStream () {
    }

    /**
     * Connect this input stream to a sender.
     * @param src	The OutpueStream to connect to.
     */
    public void connect(PipedOutputStream src) {
	src.connect(this);
    }

    /* The circular buffer into which incoming data is placed */
    private byte buffer[] = new byte[1024];

    /*
     * fill and empty pointers.  in<0 implies the buffer is empty, in==out
     * implies the buffer is full
     */
    int in = -1, out = 0;

    synchronized void recieve(int b) {
	writeSide = Thread.currentThread();
	while (in == out) {
	    if (readSide != null && !readSide.isAlive())
		throw new IOException("Pipe broken");
	    notifyAll();	/* full: kick any waiting readers */
	    wait(1000);
	}
	if (in < 0) {
	    in = 0;
	    out = 0;
	}
	buffer[in++] = (byte) b;
	if (in >= buffer.length)
	    in = 0;
    }

    synchronized void recieve(byte b[], int off, int len) {
	while (--len >= 0)
	    recieve(b[off++]);
    }

    synchronized void recievedLast() {
	closed = true;
	notifyAll();
    }

    /**
     * Reads a byte. Will block if no input is available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     */
    public synchronized int read() {
	int trials = 2;
	while (in < 0) {
	    readSide = Thread.currentThread();
	    if (writeSide != null && !writeSide.isAlive() && --trials<0)
		throw new IOException("Pipe broken");
	    if (closed)
		return -1;
	    notifyAll();	/* might be a writer waiting */
	    wait(1000);
	}
	int ret = buffer[out++];
	if (out >= buffer.length)
	    out = 0;
	if (in == out)
	    in = -1;		/* now empty */
	return ret;
    }

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * For efficiency,this method should be overridden in a subclass
     * (the default implementation reads 1 byte
     * at a time).
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public synchronized int read(byte b[], int off, int len) {
	if (len <= 0)
	    return 0;
	int c = read();		/* possibly wait on the first character */
	if (c < 0)
	    return -1;
	b[off] = (byte) c;
	int rlen = 1;
	while (in >= 0 && --len > 0) {
	    b[off + rlen] = buffer[out++];
	    rlen++;
	    if (out >= buffer.length)
		out = 0;
	    if (in == out)
		in = -1;	/* now empty */
	}
	return rlen;
    }

    /**
     * Closes the input stream. Must be called
     * to release any resources associated with
     * the stream.
     * @exception IOException i/o error occurred
     */
    public void close() {
	in = -1;
	closed = true;
    }

}
