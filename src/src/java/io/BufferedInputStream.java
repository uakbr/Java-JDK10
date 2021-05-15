/*
 * @(#)BufferedInputStream.java	1.21 95/12/18 Arthur van Hoff
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
 * A buffered input stream. This stream lets you read in characters
 * from a stream without causing a read every time. The data
 * is read into a buffer, subsequent reads result in a fast
 * buffer access.
 *
 * @version 	1.21, 12/18/95
 * @author	Arthur van Hoff
 */
public
class BufferedInputStream extends FilterInputStream {
    /**
     * The buffer where data is stored.
     */
    protected byte buf[];

    /**
     * The number of bytes in the buffer.
     */
    protected int count;

    /**
     * The current position in the buffer.
     */
    protected int pos;
    
    /**
     * The position in the buffer of the current mark.  This mark is 
     * set to -1 if there is no current mark.
     */
    protected int markpos = -1;

    /**
     * The maximum readahead allowed after a mark() before
     * subsequent calls to reset() fail.
     */
    protected int marklimit;

    /**
     * Creates a new buffered stream with a default
     * buffer size.
     * @param in 	the input stream
     */
    public BufferedInputStream(InputStream in) {
	this(in, 2048);
    }

    /**
     * Creates a new buffered stream with the specified
     * buffer size.
     * @param in 		the input stream
     * @param size	the buffer size
     */
    public BufferedInputStream(InputStream in, int size) {
	super(in);
	buf = new byte[size];
    }

    /**
     * Fills the buffer with more data, taking into account
     * shuffling and other tricks for dealing with marks.
     * Assumes that it is being called by a synchronized method.
     * This method also assumes that all data has already been read in,
     * hence pos > count.
     */
    private void fill() throws IOException {
	if (markpos < 0)
	    pos = 0;		/* no mark: throw away the buffer */
	else if (pos >= buf.length)	/* no room left in buffer */
	    if (markpos > 0) {	/* can throw away early part of the buffer */
		int sz = pos - markpos;
		System.arraycopy(buf, markpos, buf, 0, sz);
		pos = sz;
		markpos = 0;
	    } else if (buf.length >= marklimit) {
		markpos = -1;	/* buffer got too big, invalidate mark */
		pos = 0;	/* drop buffer contents */
	    } else {		/* grow buffer */
		int nsz = pos * 2;
		if (nsz > marklimit)
		    nsz = marklimit;
		byte nbuf[] = new byte[nsz];
		System.arraycopy(buf, 0, nbuf, 0, pos);
		buf = nbuf;
	    }
	int n = in.read(buf, pos, buf.length - pos);
	count = n <= 0 ? 0 : n + pos;
    }


    /**
     * Reads a byte of data. This method will block if no input is 
     * available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public synchronized int read() throws IOException {
	if (pos >= count) {
	    fill();
	    if (count == 0)
		return -1;
	}
	return buf[pos++] & 0xff;
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
    public synchronized int read(byte b[], int off, int len) throws IOException {
	int avail = count - pos;
	if (avail <= 0) {
	    fill();
	    avail = count - pos;
	    if (avail <= 0)
		return -1;
	}
	int cnt = (avail < len) ? avail : len;
	System.arraycopy(buf, pos, b, off, cnt);
	pos += cnt;
	return cnt;
    }

    /**
     * Skips n bytes of input.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public synchronized long skip(long n) throws IOException {
	long avail = count - pos;

	if (avail >= n) {
	    pos += n;
	    return n;
	}

	pos += avail;
	return avail + in.skip(n - avail);
    }

    /**
     * Returns the number of bytes that can be read
     * without blocking. This total is the number
     * of bytes in the buffer and the number of bytes
     * available from the input stream.
     * @return the number of available bytes.
     */
    public synchronized int available() throws IOException {
	return (count - pos) + in.available();
    }

    /**
     * Marks the current position in the input stream.  A subsequent
     * call to the reset() method will reposition the stream at the last
     * marked position so that subsequent reads will re-read
     * the same bytes.  The stream promises to allow readlimit bytes
     * to be read before the mark position gets invalidated.
     * @param readlimit the maximum limit of bytes allowed to be read before the 
     * mark position becomes invalid.
     */
    public synchronized void mark(int readlimit) {
	marklimit = readlimit;
	markpos = pos;
    }

    /**
     * Repositions the stream to the last marked position.  If the
     * stream has not been marked, or if the mark has been invalidated,
     * an IOException is thrown.  Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream.  Often this is most easily done by invoking some
     * general parser.  If the stream is of the type handled by the
     * parser, it just chugs along happily.  If the stream is not of
     * that type, the parser should toss an exception when it fails.  If an exception
     * gets tossed within readlimit bytes, the parser will allow the outer code to reset
     * the stream and to try another parser.
     * @exception IOException If the stream has not been marked or if the mark has been
     * invalidated.
     */
    public synchronized void reset() throws IOException {
	if (markpos < 0)
	    throw new IOException("Resetting to invalid mark");
	pos = markpos;
    }

    /**
     * Returns a boolean indicating if this stream type supports 
     * mark/reset.
     */
    public boolean markSupported() {
	return true;
    }
}
