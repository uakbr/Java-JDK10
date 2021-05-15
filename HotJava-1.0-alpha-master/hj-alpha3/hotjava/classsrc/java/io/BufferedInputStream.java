/*
 * @(#)BufferedInputStream.java	1.14 95/01/31 Arthur van Hoff
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
 * A buffered input stream. This stream lets you read characters
 * from a stream without causing a read every time. The data
 * is read into a buffer, subsequent reads result in a fast
 * buffer access.
 *
 * @version 	1.14, 31 Jan 1995
 * @author	Arthur van Hoff
 */
public
class BufferedInputStream extends FilterInputStream {
    /**
     * The buffer.
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
     * The position in the buffer of the current mark.
     * -1 if there is no current mark.
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
     * Creates a new buffered stream with a given
     * buffer size.
     * @param in 		the input stream
     * @param size	the buffer size
     */
    public BufferedInputStream(InputStream in, int size) {
	super(in);
	buf = new byte[size];
    }

    /**
     * Fill the buffer with more data, taking into account
     * shuffling and other tricks for dealing with marks.
     * Assumes that its being called by a syncronized method.
     * Also assumes that all data has already been read,
     * hence pos>count.
     */
    private void fill() {
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
     * Reads a byte. Will block if no input is available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException i/o error occurred
     */
    public synchronized int read() {
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
     * This method should be overridden in a subclass for
     * efficiency (the default implementation reads 1 byte
     * at a time).
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public synchronized int read(byte b[], int off, int len) {
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
     * Skips bytes of input.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     * @exception IOException i/o error occurred
     */
    public synchronized int skip(int n) {
	int avail = count - pos;

	if (avail >= n) {
	    pos += n;
	    return n;
	}

	pos += avail;
	return avail + in.skip(n - avail);
    }

    /**
     * Returns the number of bytes that can be read
     * without blocking. This is the total of the number
     * of bytes in the buffer and the number of bytes
     * available from the input stream.
     * @return the number of available bytes
     */
    public synchronized int available() {
	return (count - pos) + in.available();
    }

    /**
     * Mark the current position in the input stream.  A subsequent
     * call to reset() will reposition the stream at the last
     * marked position so that subsequent reads will re-read
     * the same bytes.  The stream promises to allow readlimit bytes
     * to be read before the mark position gets invalidated.
     */
    public synchronized void mark(int readlimit) {
	marklimit = readlimit;
	markpos = pos;
    }

    /**
     * Reposition the stream to the last marked position.  If the
     * stream has not been marked, or if the mark has been invalidated,
     * an IOException is thrown.  Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream.  Often this is most easily done by invoking some
     * general parser.  If the stream is of the type handled by the
     * parse, it just chugs along happily.  If the stream is *not* of
     * that type, the parser should toss an exception when it fails,
     * which, if it happens within readlimit bytes, allows the outer
     * code to reset the stream and try another parser.
     */
    public synchronized void reset() {
	if (markpos < 0)
	    throw new IOException("Reseting to invalid mark");
	pos = markpos;
    }

    /**
     * Return true since this stream type supports mark/reset
     */
    public boolean markSupported() {
	return true;
    }
}
