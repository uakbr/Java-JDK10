/*
 * @(#)FileOutputStream.java	1.21 95/11/15 Arthur van Hoff
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
 * File output stream, can be constructed from
 * a file descriptor or a file name.
 * @see	FileInputStream
 * @see	File
 * @version 	1.21, 11/15/95
 * @author	Arthur van Hoff
 */
public
class FileOutputStream extends OutputStream
{
    /**
     * The system dependent file descriptor. The value is
     * 1 more than actual file descriptor. This means that
     * the default value 0 indicates that the file is not open.
     */
    private FileDescriptor fd;

    /**
     * Creates an output file with the specified system dependent
     * file name.
     * @param name the system dependent file name 
     * @exception IOException If the file is not found.
     */
    public FileOutputStream(String name) throws IOException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(name);
	}
	try {
	    fd = new FileDescriptor();
	    open(name);
	} catch (IOException e) {
	    throw new FileNotFoundException(name);
	}
    }
    
    /**
     * Creates an output file with the specified File object.
     * @param file the file to be opened for reading
     * @exception IOException If the file is not found.
     */
    public FileOutputStream(File file) throws IOException {
	this(file.getPath());
    }

    /* This routine attaches a stream to existing FileDescriptor object.
     * Subclass SocketOutputStream uses this routine.
     */
    public FileOutputStream(FileDescriptor fdObj) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(fdObj);
	}
	fd = fdObj;
    }

    /**
     * Opens a file, with the specified name, for writing.
     * @param name name of file to be opened
     */
    private native void open(String name) throws IOException;

    /**
     * Writes a byte of data. This method will block until the byte is 
     * actually written.
     * @param b the byte to be written
     * @exception IOException If an I/O error has occurred.
     */
    public native void write(int b) throws IOException;

    /**
     * Writes a sub array as a sequence of bytes.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native void writeBytes(byte b[], int off, int len) throws IOException;

    /**
     * Writes an array of bytes. Will block until the bytes
     * are actually written.
     * @param b	the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[]) throws IOException {
	writeBytes(b, 0, b.length);
    }

    /**
     * Writes a sub array of bytes. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[], int off, int len) throws IOException {
	writeBytes(b, off, len);
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException If an I/O error has occurred.
     */
     public native void close() throws IOException;

     /**
      * Returns the file descriptor associated with this stream.
      * @return the file descriptor.
      */
     public final FileDescriptor getFD()  throws IOException {
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
