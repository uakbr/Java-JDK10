/*
 * @(#)FileInputStream.java	1.29 95/12/19 Arthur van Hoff
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
 * File input stream, can be constructed from
 * a file descriptor or a file name.
 * @see	FileOutputStream
 * @see	File
 * @version 	1.29, 12/19/95
 * @author	Arthur van Hoff
 */
public
class FileInputStream extends InputStream 
{
    /* File Descriptor - handle to the open file */
    private FileDescriptor fd;
    
    /**
     * Creates an input file with the specified system dependent file 
     * name.
     * @param name the system dependent file name
     * @exception IOException If the file is not found.
     */
    public FileInputStream(String name) throws FileNotFoundException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(name);
	}
	try {
	    fd = new FileDescriptor();
	    open(name);
	} catch (IOException e) {
	    throw new FileNotFoundException(name);
	}
    }
    
    /**
     * Creates an input file from the specified File object.
     * @param file the file to be opened for reading
     * @exception IOException If the file is not found.
     */
    public FileInputStream(File file) throws FileNotFoundException {
	this(file.getPath());
    }

    /* This routine attaches a stream to existing FileDescriptor object.
     * Subclass SocketInputStream uses this routine.
     */
    public FileInputStream(FileDescriptor fdObj) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(fdObj);
	}
	fd = fdObj;
    }

    /**
     * Opens the specified file for reading.
     * @param name the name of the file
     */
    private native void open(String name) throws IOException;

    /**
     * Reads a byte of data. This method will block if no input is 
     * available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public native int read() throws IOException;


    /** 
     * Reads a subarray as a sequence of bytes. 
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred. 
     */ 
    private native int readBytes(byte b[], int off, int len) throws IOException;

    /**
     * Reads data into an array of bytes.
     * This method blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read. -1 is
     * 		returned if the end of stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[]) throws IOException {
	return readBytes(b, 0, b.length);
    }

    /**
     * Reads data into an array of bytes.
     * This method blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read. -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[], int off, int len) throws IOException {
	return readBytes(b, off, len);
    }

    /**
     * Skips n bytes of input.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public native long skip(long n) throws IOException;

    /**
     * Returns the number of bytes that can be read
     * without blocking.
     * @return the number of available bytes, which is initially
     *		equal to the file size.
     */
    public native int available() throws IOException;

    /**
     * Closes the input stream. This method must be called
     * to release any resources associated with
     * the stream.
     * @exception IOException If an I/O error has occurred.
     */
    public native void close() throws IOException;

    /**
     * Returns the opaque file descriptor object associated with this stream.
     * @return the file descriptor.
     */
    public final FileDescriptor getFD() throws IOException {
	if (fd != null) return fd;
	throw new IOException();
    }

    /**
     * Closes the stream when garbage is collected.
     */
    protected void finalize() throws IOException {
	if (fd != null) close();
    }
}


