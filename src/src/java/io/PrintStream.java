/*
 * @(#)PrintStream.java	1.24 95/12/19 Arthur van Hoff
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
 *
 * @version 	1.24, 12/19/95
 * @author	Arthur van Hoff
 */
public
class PrintStream extends FilterOutputStream {
    private boolean autoflush;
    private boolean trouble;

    /**
     * Creates a new PrintStream.
     * @param out	the output stream
     */
    public PrintStream(OutputStream out) {
	this(out, false);
	trouble = false;
    }

    /**
     * Creates a new PrintStream, with auto flushing.
     * @param out	the output stream
     * @param autoflush if true the stream automatically flushes
     *		its output when a newline character is printed
     */
    public PrintStream(OutputStream out, boolean autoflush) {
	super(out);
	this.autoflush = autoflush;
	trouble = false;
    }

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     * @param b the byte
     * @exception IOException If an I/O error has occurred.
     */
    public void write(int b) {
        try {
	    out.write(b);
	    if (autoflush && (b == '\n')) {
	        out.flush();
	    }
  	} catch (InterruptedIOException ex) {
	    // We've been interrupted.  Make sure we're still interrupted.
	    Thread.currentThread().interrupt();
	} catch (IOException ex) {
	    trouble = true;
	}
    }

    /**
     * Writes a sub array of bytes. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[], int off, int len) {
	try {
	    out.write(b, off, len);
	    if (autoflush) {
	        out.flush();
	    }
  	} catch (InterruptedIOException ex) {
	    // We've been interrupted.  Make sure we're still interrupted.
	    Thread.currentThread().interrupt();
	} catch (IOException ex) {
	    trouble = true;
	}
    }

    /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     */
    public void flush() {
	try {
	    super.flush();
	} catch (IOException ex) {
	    trouble = true;
	}
    }

    /**
     * Closes the stream.
     */
    public void close() {
	try {
	    super.close();
	} catch (IOException ex) {
	    trouble = true;
	}
    }

    /**
     * Flushes the print stream and returns whether or not there was
     * an error on the output stream.  Errors are cumulative; once the
     * print stream encounters an error this routine will continue to
     * return true on all successive calls.
     * @return true if the print stream has ever encountered an error
     * on the output stream.
     */
    public boolean checkError() {
	flush();
	return trouble;
    }

    /**
     * Prints an object.
     * @param obj the object to be printed
     */
    public void print(Object obj) {
	print(String.valueOf(obj));
    }

    /**
     * Prints a String.
     * @param s the String to be printed
     */
    synchronized public void print(String s) {
	if (s == null) {
	    s = "null";
	}

	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    write(s.charAt(i));
	}
    }

    /**
     * Prints an array of characters.
     * @param s the array of chars to be printed
     */
    synchronized public void print(char s[]) {
	for (int i = 0 ; i < s.length ; i++) {
	    write(s[i]);
	}
    }

    /**
     * Prints an character.
     * @param c the character to be printed
     */
    public void print(char c) {
	print(String.valueOf(c));
    }

    /**
     * Prints an integer.
     * @param i the integer to be printed
     */
    public void print(int i) {
	print(String.valueOf(i));
    }

    /**
     * Prints a long.
     * @param l the long to be printed.
     */
    public void print(long l) {
	print(String.valueOf(l));
    }

    /**
     * Prints a float.
     * @param f the float to be printed
     */
    public void print(float f) {
	print(String.valueOf(f));
    }

    /**
     * Prints a double.
     * @param d the double to be printed
     */
    public void print(double d) {
	print(String.valueOf(d));
    }

    /**
     * Prints a boolean.
     * @param b the boolean to be printed
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
     * @param obj the object to be printed
     */
    synchronized public void println(Object obj) {
	print(obj);
	write('\n');
    }

    /**
     * Prints a string followed by a newline.
     * @param s the String to be printed
     */
    synchronized public void println(String s) {
	print(s);
	write('\n');
    }
    
    /**
     * Prints an array of characters followed by a newline.
     * @param s the array of characters to be printed
     */
    synchronized public void println(char s[]) {
	print(s);
	write('\n');
    }
    
    /**
     * Prints a character followed by a newline.
     * @param c the character to be printed
     */
    synchronized public void println(char c) {
	print(c);
	write('\n');
    }

    /**
     * Prints an integer followed by a newline.
     * @param i the integer to be printed
     */
    synchronized public void println(int i) {
	print(i);
	write('\n');
    }

    /**
     * Prints a long followed by a newline.
     * @param l the long to be printed
     */
    synchronized public void println(long l) {
	print(l);
	write('\n');
    }

    /**
     * Prints a float followed by a newline.
     * @param f the float to be printed
     */
    synchronized public void println(float f) {
	print(f);
	write('\n');
    }

    /**
     * Prints a double followed by a newline.
     * @param d the double to be printed
     */
    synchronized public void println(double d) {
	print(d);
	write('\n');
    }

    /**
     * Prints a boolean followed by a newline.
     * @param b the boolean to be printed
     */
    synchronized public void println(boolean b) {
	print(b);
	write('\n');
    }
}
