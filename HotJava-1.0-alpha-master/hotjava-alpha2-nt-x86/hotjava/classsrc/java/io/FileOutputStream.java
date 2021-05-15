/*
 * @(#)FileOutputStream.java	1.12 95/05/03 Arthur van Hoff
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
 * File output stream; can be constructed from
 * a file descriptor or a file name.
 * @see	FileInputStream
 * @see	File
 * @version 	1.12, 03 May 1995
 * @author	Arthur van Hoff
 */
public
class FileOutputStream extends OutputStream {
    /**
     * The system dependent file descriptor.
     */
    private int fd = -1;

    /**
     * Creates an output file given a file name.
     * @param name the file name (very system dependent)
     * @exception IOException i/o error occurred, file not found
     */
    public FileOutputStream(String name) {
	this.fd = open(name);
    }
    
    /**
     * Creates an output file given a file descriptor.
     * @param fd	the file descriptor (very system dependent)
     * @exception IOException i/o error occurred
     */
    public FileOutputStream(int fd) {
	this.fd = openfd(fd);
    }
    
    /**
     * Creates an output file given a File object.
     * @param file the file to be opened for reading
     * @exception IOException i/o error occurred, file not found
     */
    public FileOutputStream(File file) {
	this(file.getPath());
    }
    
    /**
     * Opens a file for writing
     */
    private native int open(String name);

    /**
     * Uses a file descriptor for writing.
     */
    private native int openfd(int fd);

    /**
     * Write a byte. Will block until the byte is actually
     * written.
     * @exception IOException i/o error occurred
     */
    public native void write(int b);

    /**
     * Writes bytes
     */
    private native void writeBytes(byte b[], int off, int len);

    /**
     * Writes an array of bytes. Will block until the bytes
     * are actually written.
     * @param b	the data to be written
     * @exception IOException i/o error occurred
     */
    public void write(byte b[]) {
	writeBytes(b, 0, b.length);
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
	writeBytes(b, off, len);
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException i/o error occurred
     */
    public synchronized native void close();

    /**
     * Returns the file descriptor associated with this stream.
     */
    public final int getFD() {
	return fd;
    }

    /**
     * Close the stream when garbage is collected.
     */
    protected void finalize() {
        try {
	    close();
	} catch (Exception e) {
	}    
    }
}
