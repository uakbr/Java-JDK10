/*
 * @(#)BufferedOutputStream.java	1.12 95/01/31 Arthur van Hoff
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

/**
 * A buffered output stream. This stream lets you write characters
 * to a stream without causing a write every time. The data
 * is first written into a buffer. It is written to the
 * actual stream when the buffer is full, or when the stream is
 * flushed.
 *
 * @version 	1.12, 31 Jan 1995
 * @author	Arthur van Hoff
 */
public 
class BufferedOutputStream extends FilterOutputStream {
    /**
     * The buffer.
     */
    protected byte buf[];

    /**
     * The number of bytes in the buffer.
     */
    protected int count;
    
    /**
     * Creates a new buffered stream with a default
     * buffer size.
     * @param in 	the input stream
     */
    public BufferedOutputStream(OutputStream out) {
	this(out, 512);
    }

    /**
     * Creates a new buffered stream with a given
     * buffer size.
     * @param in 		the input stream
     * @param size	the buffer size
     */
    public BufferedOutputStream(OutputStream out, int size) {
	super(out);
	buf = new byte[size];
    }

    /**
     * Writes a byte. Will block until the byte is actually
     * written.
     * @exception IOException i/o error occurred
     */
    public synchronized void write(int b) {
	if (count == buf.length) {
	    flush();
	}
	buf[count++] = (byte)b;
    }

    /**
     * Writes a sub array of bytes. To be efficient it should
     * be overridden in a subclass. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException i/o error occurred
     */
    public synchronized void write(byte b[], int off, int len) {
	int avail = buf.length - count;

	if (len <= avail) {
	    System.arraycopy(b, off, buf, count, len);
	    count += len;
	    return;
	}

	flush();
	out.write(b, off, len);
    }

    /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     * @exception IOException i/o error occurred
     */
    public synchronized void flush() {
	out.write(buf, 0, count);
	out.flush();
	count = 0;
    }
}
