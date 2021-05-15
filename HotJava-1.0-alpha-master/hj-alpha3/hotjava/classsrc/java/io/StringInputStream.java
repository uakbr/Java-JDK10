/*
 * @(#)StringInputStream.java	1.1 95/05/10 James Gosling
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

package java.io;
import java.io.File;

/**
 * String input stream, can be constructed from a String.
 * Allows a String to be read as though it's a Stream.
 * @author	James Gosling
 */
public
class StringInputStream extends InputStream {

    private String s;
    private int pos;

    /**
     * Creates an input file given a string.
     */
    public StringInputStream (String str) {
	s = str;
    }

    /**
     * Reads a byte.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     */
    public synchronized int read() {
	try {
	    return s.charAt(pos++);
	} catch(Exception e) {
	    return -1;
	}
    }

    /**
     * Skips bytes of input.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     * @exception IOException i/o error occurred
     */
    public synchronized int skip(int n) {
	int lpos = pos + n;
	int limit = s.length();
	if (lpos > limit) {
	    n = limit - pos;
	    lpos = limit;
	}
	pos = lpos;
	return n;
    }

    /**
     * Returns the number of bytes that can be read
     * without blocking.
     * @return the number of available bytes, which is initially
     *		equal to the file size
     */
    public synchronized int available() {
	int ret = (s != null ? 0 : s.length()) - pos;
	return ret <= 0 ? 0 : ret;
    }

    /**
     * Closes the input stream.
     * @exception IOException i/o error occurred
     */
    public synchronized void close() {
	s = null;
    }
}
