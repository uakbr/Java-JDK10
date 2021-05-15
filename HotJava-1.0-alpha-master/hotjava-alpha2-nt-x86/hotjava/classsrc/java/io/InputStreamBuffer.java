/*
 * @(#)InputStreamBuffer.java	1.11 95/01/31 Arthur van Hoff
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
 * used as an InputStream. 
 * @version 	1.11, 31 Jan 1995
 * @author	Arthur van Hoff
 */
public
class InputStreamBuffer extends InputStream {
    /**
     * The buffer.
     */
    protected byte buf[];

    /**
     * The position in the buffer.
     */
    protected int pos;

    /**
     * The number of characters to use in the buffer.
     */
    protected int count;

    /**
     * Creates an InputStreamBuffer, given an array of bytes.
     * @param buf	the input buffer (not copied)
     */
    public InputStreamBuffer(byte buf[]) {
	this.buf = buf;
	count = buf.length;
    }

    /**
     * Creates an InputStreamBuffer, given an array of bytes.
     * @param buf	the input buffer (not copied)
     * @param length	the number of bytes in the buffer
     */
    public InputStreamBuffer(byte buf[], int length) {
	this.buf = buf;
	count = length;
    }

    /**
     * Reads a byte.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     */
    public synchronized int read() {
	return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * Reads into an array of bytes.
     * For efficiency, this method should be overridden in a 
     * subclass (the default implementation reads 1 byte
     * at a time).
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read; -1 is
     * 		returned when the end of the stream is reached.
     */
    public synchronized int read(byte b[], int off, int len) {
	if (pos >= count) {
	    return -1;
	}
	if (pos + len > count) {
	    len = count - pos;
	}
	if (len <= 0) {
	    return 0;
	}
	System.arraycopy(buf, pos, b, off, len);
	pos += len;
	return len;
    }

    /**
     * Skips bytes of input.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     */
    public synchronized int skip(int n) {
	if (pos + n > count) {
	    n = count - pos;
	}
	if (n < 0) {
	    return 0;
	}
	pos += n;
	return n;
    }

    /**
     * Returns the number of available bytes in the buffer.
     * @return the number of available bytes
     */
    public synchronized int available() {
	return count - pos;
    }

    /**
     * Resets the buffer to the beginning.
     */
    public synchronized void reset() {
	pos = 0;
    }
}
