/*
 * @(#)PushbackInputStream.java	1.6 95/01/31 Jonathan Payne
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
 * An input stream that has a 1 byte push back buffer.
 * It also keeps track of line numbers.
 * @version 	1.6, 31 Jan 1995
 * @author	Jonathan Payne
 */
public
class PushbackInputStream extends FilterInputStream {
    /**
     * Push back character.
     */
    protected int	pushBack = -1;

    /**
     * The current line number.
     */
    protected int	lineNumber = 1;

    /**
     * Creates a PushbackInputStream.
     * @param in the input stream
     */
    public PushbackInputStream(InputStream in) {
	super(in);
    }

    /**
     * Reads a byte. Will block if no input is available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException i/o error occurred
     */
    public int read() {
	int c;

	if ((c = pushBack) != -1) {
	    pushBack = -1;
	} else {
	    c = super.read();
	}
	if (c == '\n') {
	    lineNumber += 1;
	}
	return c;
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
    public int read(byte bytes[], int offset, int length) {
	int c = read();
	if (c == -1) {
	    bytes[offset] = (byte)c;
	    return 1;
	}
	return -1;
    }

    /**
     * Pushes back a character. 
     * @param ch the character to push back.
     * @exception Exception Attempt to push back more than one character.
     */
    public void unread(int ch) {
	if (pushBack != -1) {
	    throw new Exception("Attempt to unread more than one character!");
	}
	pushBack = ch;
    }

    /**
     * Returns the current line number.
     */
    public int currentLine() {
	return lineNumber;
    }
}
