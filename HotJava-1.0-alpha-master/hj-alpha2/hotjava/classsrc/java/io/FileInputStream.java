/*
 * @(#)FileInputStream.java	1.13 95/02/17 Arthur van Hoff
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
 * File input stream, can be constructed from
 * a file descriptor or a file name.
 * @see	FileOutputStream
 * @see	File
 * @version 	1.13, 17 Feb 1995
 * @author	Arthur van Hoff
 */
public
class FileInputStream extends InputStream {
    /**
     * The system-dependent file descriptor.
     */
    private int fd;

    /**
     * Creates an input file given a file name.
     * @param name the file name (very system dependent)
     * @exception IOException i/o error occurred, file not found
     */
    public FileInputStream(String name) {
	this.fd = open(name);
    }
    
    /**
     * Creates an input file given a file descriptor.
     * @param fd	the file descriptor (very system dependent)
     * @exception IOException i/o error occurred
     */
    public FileInputStream(int fd) {
	this.fd = openfd(fd);
    }
    
    /**
     * Creates an input file given a File object.
     * @param file the file to be opened for reading
     * @exception IOException i/o error occurred, file not found
     */
    public FileInputStream(File file) {
	this(file.getPath());
    }

    /**
     * Opens a file for reading.
     */
    private native int open(String name);

    /**
     * Uses a file descriptor for reading.
     */
    private native int openfd(int fd);

    /**
     * Reads a byte. Will block if no input is available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException i/o error occurred
     */
    public native int read();

    private native int readBytes(byte b[], int off, int len);

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public int read(byte b[]) {
	return readBytes(b, 0, b.length);
    }

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * For efficiency,this method should be overridden in a subclass
     * (the default implementation reads 1 byte
     * at a time).
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public int read(byte b[], int off, int len) {
	return readBytes(b, off, len);
    }

    /**
     * Skips bytes of input.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     * @exception IOException i/o error occurred
     */
    public native int skip(int n);

    /**
     * Returns the number of bytes that can be read
     * without blocking.
     * @return the number of available bytes, which is initially
     *		equal to the file size
     */
    public native int available();

    /**
     * Closes the input stream. Must be called
     * to release any resources associated with
     * the stream.
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
