/*
 * @(#)PrintStream.java	1.15 95/02/21 Arthur van Hoff
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
 * This class implements an output stream that has
 * additional methods for printing. You can specify
 * that the stream should be flushed every time a
 * newline character is written.<p>
 *
 * <em>The top byte of 16 bit characters is discarded.</em><p>
 * Example:
 * <pre>
 *	System.out.println("Hello world!");
 *	System.out.print("x = ");
 *	System.out.println(x);
 *	System.out.println("y = " + y);
 * </pre>
 * @version 	1.15, 21 Feb 1995
 * @author	Arthur van Hoff
 */
public
class PrintStream extends FilterOutputStream {
    private boolean autoflush;

    /**
     * Creates a new PrintStream.
     * @param out	the output stream
     */
    public PrintStream(OutputStream out) {
	this(out, false);
    }

    /**
     * Creates a new PrintStream, with auto flushing.
     * @param out	the output stream
     * @param autoflush if true the stream automatically flushes
     *		its output when a newline character is printed.
     */
    public PrintStream(OutputStream out, boolean autoflush) {
	super(out);
	this.autoflush = autoflush;
    }

    /**
     * Writes a byte. Will block until the byte is actually
     * written.
     * @param b the byte
     * @exception IOException i/o error occurred
     */
    public void write(int b) {
	out.write(b);
	if (autoflush && (b == '\n')) {
	    out.flush();
	}
    }

    /**
     * Writes a sub array of bytes. To be efficient it should
     * be overridden in a subclass. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException i/o error occurred
     */
    public void write(byte b[], int off, int len) {
	out.write(b, off, len);
	if (autoflush) {
	    out.flush();
	}
    }
    
    /**
     * Prints an object.
     */
    public void print(Object obj) {
	print(String.valueOf(obj));
    }

    /**
     * Prints a String.
     */
    synchronized public void print(String s) {
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    write(s.charAt(i));
	}
    }

    /**
     * Prints an array of characters.
     */
    synchronized public void print(char s[]) {
	for (int i = 0 ; i < s.length ; i++) {
	    write(s[i]);
	}
    }

    /**
     * Prints an integer.
     */
    public void print(int i) {
	print(String.valueOf(i));
    }

    /**
     * Prints a long.
     */
    public void print(long l) {
	print(String.valueOf(l));
    }

    /**
     * Prints a float.
     */
    public void print(float f) {
	print(String.valueOf(f));
    }

    /**
     * Prints a double.
     */
    public void print(double d) {
	print(String.valueOf(d));
    }

    /**
     * Prints a boolean.
     */
    public void print(boolean b) {
	print(b ? "true" : "false");
    }
    
    /**
     * Prints a newline.
     */
    public void println() {
	write('\n');
    }
    
    /**
     * Prints an object followed by a newline.
     */
    synchronized public void println(Object obj) {
	print(obj);
	write('\n');
    }

    /**
     * Prints a string followed by a newline.
     */
    synchronized public void println(String s) {
	print(s);
	write('\n');
    }
    
    /**
     * Prints an array of characters followed by a newline.
     */
    synchronized public void println(char s[]) {
	print(s);
	write('\n');
    }

    /**
     * Prints an integer followed by a newline.
     */
    synchronized public void println(int i) {
	print(i);
	write('\n');
    }

    /**
     * Prints a long followed by a newline.
     */
    synchronized public void println(long l) {
	print(l);
	write('\n');
    }

    /**
     * Prints a float followed by a newline.
     */
    synchronized public void println(float f) {
	print(f);
	write('\n');
    }

    /**
     * Prints a double followed by a newline.
     */
    synchronized public void println(double d) {
	print(d);
	write('\n');
    }

    /**
     * Prints a boolean followed by a newline.
     */
    synchronized public void println(boolean b) {
	print(b);
	write('\n');
    }
}
