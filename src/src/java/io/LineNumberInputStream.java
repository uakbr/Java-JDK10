/*
 * @(#)LineNumberInputStream.java	1.8 95/12/19 Arthur van Hoff
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
 * An input stream that keeps track of line numbers.
 *
 * @version 	1.8, 12/19/95
 * @author	Arthur van Hoff
 */
public
class LineNumberInputStream extends FilterInputStream {
    int pushBack = -1;
    int lineNumber;
    int markLineNumber;

    /**
     * Constructs a new LineNumberInputStream initialized with
     * the specified input stream.
     * @param in the input stream
     */
    public LineNumberInputStream(InputStream in) {
	super(in);
    }

    /**
     * Reads a byte of data. The method will block if no input is 
     * available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read() throws IOException {
	int c = pushBack;

	if (c != -1) {
	    pushBack = -1;
	} else {
	    c = in.read();
	}

	switch (c) {
	  case '\r':
	    pushBack = in.read();
	    if (pushBack == '\n') {
		pushBack = -1;
	    }
	  case '\n':
	    lineNumber++;
	    return '\n';
	}
	return c;
    }

    /**
     * Reads into an array of bytes.  This method will
     * blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[], int off, int len) throws IOException {
	if (len <= 0) {
	    return 0;
	}

	int c = read();
	if (c == -1) {
	    return -1;
	}
	b[off] = (byte)c;

	int i = 1;
	try {
	    for (; i < len ; i++) {
		c = read();
		if (c == -1) {
		    break;
		}
		if (b != null) {
		    b[off + i] = (byte)c;
		}
	    }
	} catch (IOException ee) {
	}
	return i;
    }

    /**
     * Sets the current line number.
     * @param lineNumber the line number to be set
     */
    public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
    }

    /**
     * Returns the current line number.
     */
    public int getLineNumber() {
	return lineNumber;
    }

    /**
     * Skips n bytes of input.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public long skip(long n) throws IOException {
	return read(new byte[(int)n]);
    }

    /**
     * Returns the number of bytes that can be read 
     * without blocking.
     * @return the number of available bytes
     */
    public int available() throws IOException {
	return (pushBack == -1) ? super.available() : super.available() + 1;
    }

    /**
     * Marks the current position in the input stream.  A subsequent
     * call to reset() will reposition the stream at the last
     * marked position so that subsequent reads will re-read
     * the same bytes.  The stream promises to allow readlimit bytes
     * to be read before the mark position gets invalidated.
     * 
     * @param readlimit the maximum limit of bytes allowed to be read
     * before the mark position becomes invalid.
     */
    public void mark(int readlimit) {
	markLineNumber = lineNumber;
	in.mark(readlimit);
    }

    /**
     * Repositions the stream to the last marked position.  If the
     * stream has not been marked, or if the mark has been invalidated,
     * an IOException is thrown.  Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream.  Often this is most easily done by invoking some
     * general parser.  If the stream is of the type handled by the
     * parser, it just chugs along happily.  If the stream is not of
     * that type, the parser should toss an exception when it fails,
     * which, if it happens within readlimit bytes, allows the outer
     * code to reset the stream and try another parser.
     */
    public void reset() throws IOException {
	lineNumber = markLineNumber;
	in.reset();
    }
}
