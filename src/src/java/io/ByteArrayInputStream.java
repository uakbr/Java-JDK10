/*
 * @(#)ByteArrayInputStream.java	1.16 95/08/11 Arthur van Hoff
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
 * @version 	1.16, 08/11/95
 * @author	Arthur van Hoff
 */
public
class ByteArrayInputStream extends InputStream {
    /**
     * The buffer where data is stored.
     */
    protected byte buf[];

    /**
     * The current position in the buffer.
     */
    protected int pos;

    /**
     * The number of characters to use in the buffer.
     */
    protected int count;

    /**
     * Creates an ByteArrayInputStream from the specified array of bytes.
     * @param buf	the input buffer (not copied)
     */
    public ByteArrayInputStream(byte buf[]) {
	this.buf = buf;
        this.pos = 0;
	this.count = buf.length;
    }

    /**
     * Creates an ByteArrayInputStream from the specified array of bytes.
     * @param buf	the input buffer (not copied)
     * @param offset    the offset of the first byte to read
     * @param length	the number of bytes to read
     */
    public ByteArrayInputStream(byte buf[], int offset, int length) {
	this.buf = buf;
        this.pos = offset;
	this.count = Math.min(offset + length, buf.length);
    }

    /**
     * Reads a byte of data.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     */
    public synchronized int read() {
	return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * Reads into an array of bytes.
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
     * Skips n bytes of input.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     */
    public synchronized long skip(long n) {
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
