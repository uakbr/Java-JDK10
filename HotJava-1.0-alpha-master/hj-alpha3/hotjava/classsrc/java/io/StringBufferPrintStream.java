/*
 * 95/05/12 @(#)StringBufferPrintStream.java	1.3 James Gosling
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
import java.io.File;

/**
 * File output stream constructed from a StringBuffer.
 * This lets you create an output stream and capture its output in
 * a String or StringBuffer.
 * @author	James Gosling
 */
public
class StringBufferPrintStream extends PrintStream {
    private StringBuffer sb;

    /**
     * Creates an output file given a StringBuffer.
     */
    public StringBufferPrintStream(StringBuffer fsb) {
	super(null);
	sb = fsb;
    }
   
    /**
    /**
     * Creates an output file and a StringBuffer.
     * Use toString or getStringBuffer to fetch the results.
     */
    public StringBufferPrintStream() {
	super(null);
	sb = new StringBuffer();;
    }
   
    /**
     * Write a byte.
     */
    public void write(int b) {
	sb.appendChar(b);
    }

    /**
     * Closes the stream.
     */
    public void close() {
	sb = null;
    }

    /**
     * Return the contents of the output stream as a String
     */
    public String toString() {
	return sb==null ? "" : sb.toString();
    }

    /**
     * Return the string buffer associated with the output stream.
     */
    public StringBuffer getStringBuffer() {
	return sb;
    }

    /**
     * Prints a String.
     */
    public void print(String s) {
	sb.append(s);
    }

    /**
     * Prints an array of characters.
     */
    public void print(char s[]) {
	sb.append(s);
    }
}
