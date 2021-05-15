/*
 * @(#)ByteArrayOutputStream.java	1.16 95/12/18 Arthur van Hoff
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
 * This class implements a buffer that can be
 * used as an OutputStream. The buffer automatically
 * grows when data is written to the stream.
 * The data can be retrieved using toByteArray() and
 * toString().
 * @version 	1.16, 12/18/95
 * @author	Arthur van Hoff
 */
public
class ByteArrayOutputStream extends OutputStream {
    /** 
     * The buffer where data is stored.
     */
    protected byte buf[];

    /**
     * The number of bytes in the buffer.
     */
    protected int count;

    /**
     * Creates a new ByteArrayOutputStream.
     */
    public ByteArrayOutputStream() {
	this(32);
    }

    /**
     * Creates a new ByteArrayOutputStream with the specified initial size.
     * @param size the initial size
     */
    public ByteArrayOutputStream(int size) {
	buf = new byte[size];
    }

    /**
     * Writes a byte to the buffer.
     * @param b	the byte
     */
    public synchronized void write(int b) {
	int newcount = count + 1;
	if (newcount > buf.length) {
	    byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
	    System.arraycopy(buf, 0, newbuf, 0, count);
	    buf = newbuf;
	}
	buf[count] = (byte)b;
	count = newcount;
    }

    /**
     * Writes bytes to the buffer.
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     */
    public synchronized void write(byte b[], int off, int len) {
	int newcount = count + len;
	if (newcount > buf.length) {
	    byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
	    System.arraycopy(buf, 0, newbuf, 0, count);
	    buf = newbuf;
	}
	System.arraycopy(b, off, buf, count, len);
	count = newcount;
    }

    /**
     * Writes the contents of the buffer to another stream.
     * @param out	the output stream to write to
     */
    public synchronized void writeTo(OutputStream out) throws IOException {
	out.write(buf, 0, count);
    }

    /**
     * Resets the buffer so that you can use it again without
     * throwing away the already allocated buffer.
     */
    public synchronized void reset() {
	count = 0;
    }

    /**
     * Returns a copy of the input data.
     */
    public synchronized byte toByteArray()[] {
	byte newbuf[] = new byte[count];
	System.arraycopy(buf, 0, newbuf, 0, count);
	return newbuf;
    }

    /**
     * Returns the current size of the buffer.
     */
    public int size() {
	return count;
    }

    /**
     * Converts input data to a string.
     * @return the string.
     */
    public String toString() {
	return new String(toByteArray(), 0);
    }

    /**
     * Converts input data to a string. The top 8 bits of 
     * each 16 bit Unicode character are set to hibyte.
     * @param hibyte the bits set
     */
    public String toString(int hibyte) {
	return new String(toByteArray(), hibyte);
    }
}
