/*
 * @(#)PushbackInputStream.java	1.11 95/08/10 Jonathan Payne
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
 *
 * @version 	1.11, 08/10/95
 * @author	Jonathan Payne
 */
public
class PushbackInputStream extends FilterInputStream {
    /**
     * Push back character.
     */
    protected int	pushBack = -1;

    /**
     * Creates a PushbackInputStream.
     * @param in the input stream
     */
    public PushbackInputStream(InputStream in) {
	super(in);
    }

    /**
     * Reads a byte of data. This method will block if no input is 
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
	return c;
    }

    /**
     * Reads into an array of bytes.  This method 
     * blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte bytes[], int offset, int length) throws IOException {
	if (pushBack != -1) {
	    if (length == 0) {
		return 0;
	    }
	    bytes[offset] = (byte)pushBack;
	    pushBack = -1;
	    return 1;
	}
	return in.read(bytes, offset, length);
    }

    /**
     * Pushes back a character. 
     * @param ch the character to push back.
     * @exception IOException If an attempt to push back more than one 
     * character is made.
     */
    public void unread(int ch) throws IOException {
	if (pushBack != -1) {
	    throw new IOException("Attempt to unread more than one character!");
	}
	pushBack = ch;
    }

    /**
     * Returns the number of bytes that can be read.
     * without blocking.
     */
    public int available() throws IOException {
	return (pushBack == -1) ? super.available() : super.available() + 1;
    }

    /**
     * Returns true if this stream type supports mark/reset.
     */
    public boolean markSupported() {
	return false;
    }
}
