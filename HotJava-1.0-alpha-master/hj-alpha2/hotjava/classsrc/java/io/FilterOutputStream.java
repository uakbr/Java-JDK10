/*
 * @(#)FilterOutputStream.java	1.7 95/01/31 Jonathan Payne
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
 * Abstract class representing a filtered output stream of bytes.
 * This class is the basis for enhancing output stream functionality.
 * It allows multiple output stream filters to be chained together,
 * each providing additional functionality.
 * @version 	1.7, 31 Jan 1995
 * @author	Jonathan Payne
 */
public
class FilterOutputStream extends OutputStream {
    /**
     * The actual output stream.
     */
    protected OutputStream out;

    /**
     * Create an output stream filter.
     * @param out	the output stream
     */
    public FilterOutputStream(OutputStream out) {
	this.out = out;
    }

    /**
     * Writes a byte. Will block until the byte is actually
     * written.
     * @param b the byte
     * @exception IOException i/o error occurred
     */
    public void write(int b) {
	out.write(b);
    }

    /**
     * Writes an array of bytes. Will block until the bytes
     * are actually written.
     * @param b	the data to be written
     * @exception IOException i/o error occurred
     */
    public void write(byte b[]) {
	write(b, 0, b.length);
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
	for (int i = 0 ; i < len ; i++) {
	    out.write(b[off + i]);
	}
    }

    /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     * @exception IOException i/o error occurred
     */
    public void flush() {
	out.flush();
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException i/o error occurred
     */
    public void close() {
	out.flush();
	out.close();
    }
}
