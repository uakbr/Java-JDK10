/*
 * @(#)FilterInputStream.java	1.8 95/01/31 Jonathan Payne
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
 * Abstract class representing a filtered input stream of bytes.
 * This class is the basis for enhancing input stream functionality.
 * It allows multiple input stream filters to be chained together,
 * each providing additional functionality.
 * @version 	1.8, 31 Jan 1995
 * @author	Jonathan Payne
 */
public
class FilterInputStream extends InputStream {
    /**
     * The actual input stream.
     */
    protected InputStream in;

    /**
     * Creates an input stream filter.
     * @param in	the input stream
     */
    protected FilterInputStream(InputStream in) {
	this.in = in;
    }

    /**
     * Reads a byte. Will block if no input is available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException i/o error occurred
     */
    public int read() {
	return in.read();
    }

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public int read(byte b[]) {
	return read(b, 0, b.length);
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
    public int read(byte b[], int off, int len) {
	return in.read(b, off, len);
    }

    /**
     * Skips bytes of input.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     * @exception IOException i/o error occurred
     */
    public int skip(int n) {
	return in.skip(n);
    }

    /**
     * Returns the number of bytes that can be read.
     * without blocking.
     * @return the number of available bytes
     */
    public int available() {
	return in.available();
    }

    /**
     * Closes the input stream. Must be called
     * to release any resources associated with
     * the stream.
     * @exception IOException i/o error occurred
     */
    public void close() {
	in.close();
    }

    /**
     * Marks the current position in the input stream.  A subsequent
     * call to reset() will reposition the stream at the last
     * marked position so that subsequent reads will re-read
     * the same bytes.  The stream promises to allow readlimit bytes
     * to be read before the mark position gets invalidated.
     */
    public synchronized void mark(int readlimit) {
	in.mark(readlimit);
    }

    /**
     * Repositions the stream to the last marked position.  If the
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
	in.reset();
    }

    /**
     * Returns true if this stream type supports mark/reset
     */
    public boolean markSupported() {
	return in.markSupported();
    }
}
