/*
 * @(#)RandomAccessFile.java	1.9 95/01/31 David Brown
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

/*
 * Random Access File.  Read-only or read-write modes.
 * Can be constructed from a file descriptor, file name, or File object.
 * This needs much more thought...
 */
public
class RandomAccessFile {
    int fd;

    /**
     * Create a RandomAccessFile given a file name.
     * Mode is "r" for read-only, "rw" for read+write.
     * May throw IOException. The file name is 
     * very system dependent!
     */
    public RandomAccessFile(String name, String mode) {
	this(open(name, mode.equals("rw")));
    }
    
    /**
     * Create a RandomAccessFile given a file descriptor.
     * May throw IOException. The use of file descriptor 
     * is very system dependent!
     */
    public RandomAccessFile(int fd) {
	this.fd = fd;
    }
    
    /**
     * Create a RandomAccessFile given a File object
     * and mode ("r" or "rw").     
     */
    public RandomAccessFile(File file, String mode) {
	this(file.getPath(), mode);
    }

    /**
     * Open a file, return the fd.  File is opened in
     * read-write mode if writeable is true, else read-only.
     */
    private native int open(String name, boolean writeable);

    // 'Read' primitives
    
    /**
     * Read a byte
     */
    public native int read();

    /**
     * Read bytes
     */
    private native int readBytes(byte b[], int off, int len);

    /**
     * Read an array of bytes
     */
    public int read(byte b[]) {
	return readBytes(b, 0, b.length);
    }
    public int read(byte b[], int off, int len) {
	return readBytes(b, off, len);
    }

    // 'Write' primitives

    /**
     * Write a byte.
     */
    public native void write(int b);

    /**
     * Write bytes
     */
    private native void writeBytes(byte b[], int off, int len);

    /**
     * Write an array of bytes
     */
    public void write(byte b[]) {
	writeBytes(b, 0, b.length);
    }
    public void write(byte b[], int off, int len) {
	writeBytes(b, off, len);
    }

    // 'Random access' stuff

    /**
     * Return the current location of the file pointer
     */
    public native int getFilePointer();

    /**
     * Set the file pointer to the specified absolute position
     */
    public native void seek(int pos);

    /**
     * Return the length of the file.
     */
    public native int length();

    /**
     * Close the file. 
     * (We need destructors!)
     */
    public native void close();


    //
    //  Some "reading/writing Java data types" methods stolen from
    //  DataInputStream and DataOutputStream.
    //
    //    REMIND:  These don't belong here!  There eventually should be
    //  a way to get a DataInputStream or DataOutputStream
    //  based on a RandomAccessFile, or instead we could an interface
    //  which describes the basic read() or write() functionality
    //  (and then have a "PortableDataTypeIO" utility class which
    //  had all the code to read/write primitive Java data types...)
    //

    /**
     * Read a boolean
     */
    public final boolean readBoolean() {
	return read() != 0;
    }

    /**
     * Read a byte
     */
    public final byte readByte() {
	return (byte)read();
    }

    /**
     * Read short
     */
    public final short readShort() {
	return (short)(((read() << 8) & (0xFF << 8)) |
		       ((read() << 0) & (0xFF << 0)));
    }

    /**
     * Read char
     */
    public final char readChar() {
	return (char)(((read() << 8) & (0xFF << 8)) |
		      ((read() << 0) & (0xFF << 0)));

    }

    /**
     * Read int
     */
    public final int readInt() {
	return ((read() << 24) & (0xFF << 24)) |
	       ((read() << 16) & (0xFF << 16)) |
	       ((read() <<  8) & (0xFF <<  8)) |
	       ((read() <<  0) & (0xFF <<  0));
    }

    /**
     * Read long
     */
    public final long readLong() {
	return (((long)read() << 56) & ((long)0xFF << 56)) |
	       (((long)read() << 48) & ((long)0xFF << 48)) |
	       (((long)read() << 40) & ((long)0xFF << 40)) |
	       (((long)read() << 32) & ((long)0xFF << 32)) |
	       (((long)read() << 24) & ((long)0xFF << 24)) |
	       (((long)read() << 16) & ((long)0xFF << 16)) | 
	       (((long)read() <<  8) & ((long)0xFF <<  8)) |
	       (((long)read() <<  0) & ((long)0xFF <<  0));
    }

    /**
     * Read a line terminated by a '\n' or EOF.
     */
    public final String readLine() {
	StringBuffer input = new StringBuffer();
	int c;

	while (((c = read()) != -1) && (c != '\n')) {
	    input.appendChar((char)c);
	}
	if ((c == -1) && (input.length() == 0)) {
	    return null;
	}
	return input.toString();
    }

    /**
     * Read a UTF format string
     */
    public final String readUTF() {
	int utflen = ((read() << 8) & 0xFF00) |
	             ((read() << 0) & 0x00FF);
	char str[] = new char[utflen];
	int strlen = 0;


	for (int i = 0 ; i < utflen ;) {
	    int c = read();
	    if ((c & 0x80) == 0) {
		str[strlen++] = (char)c;
		i++;
	    } else if ((c & 0xE0) == 0xC0) {
		str[strlen++] = (char)(((c & 0x1F) << 6) | (read() & 0x3F));
		i += 2;
	    } else {
		str[strlen++] = (char)(((c & 0x0F) << 12) |
				       ((read() & 0x3F) << 6) |
				       (read() & 0x3F));
		i += 3;
	    } 
	}
	return new String(str, 0, strlen);
    }



    /**
     * Write a boolean
     */
    public final void writeBoolean(boolean v) {
	write(v ? 1 : 0);
	//written++;
    }

    /**
     * Write a byte
     */
    public final void writeByte(int v) {
	write(v);
	//written++;
    }

    /**
     * Write short
     */
    public final void writeShort(int v) {
	write((v >>> 8) & 0xFF);
	write((v >>> 0) & 0xFF);
	//written += 2;
    }

    /**
     * Write char
     */
    public final void writeChar(int v) {
	write((v >>> 8) & 0xFF);
	write((v >>> 0) & 0xFF);
	//written += 2;
    }

    /**
     * Write int
     */
    public final void writeInt(int v) {
	write((v >>> 24) & 0xFF);
	write((v >>> 16) & 0xFF);
	write((v >>>  8) & 0xFF);
	write((v >>>  0) & 0xFF);
	//written += 4;
    }

    /**
     * Write long
     */
    public final void writeLong(long v) {
	write((int)(v >>> 56) & 0xFF);
	write((int)(v >>> 48) & 0xFF);
	write((int)(v >>> 40) & 0xFF);
	write((int)(v >>> 32) & 0xFF);
	write((int)(v >>> 24) & 0xFF);
	write((int)(v >>> 16) & 0xFF);
	write((int)(v >>>  8) & 0xFF);
	write((int)(v >>>  0) & 0xFF);
	//written += 8;
    }

    /**
     * Write a string as a sequence of bytes
     * REMIND: need to add support for more data types
     */
    public final void writeBytes(String s) {
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    write((byte)s.charAt(i));
	}
	//written += len;
    }

    /**
     * Write a string as a sequence of chars
     */
    public final void writeChars(String s) {
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    int v = s.charAt(i);
	    write((v >>> 8) & 0xFF);
	    write((v >>> 0) & 0xFF);
	}
	//written += len * 2;
    }

    /**
     * Write string in UTF format
     */
    public final void writeUTF(String str) {
	int strlen = str.length();
	int utflen = 0;

	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x03FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}

	write((utflen >>> 8) & 0xFF);
	write((utflen >>> 0) & 0xFF);
	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		write(c);
	    } else if (c > 0x03FF) {
		write(0xE0 | ((c >> 12) & 0x0F));
		write(0x80 | ((c >>  6) & 0x3F));
		write(0x80 | ((c >>  0) & 0x3F));
		//written += 2;
	    } else {
		write(0xC0 | ((c >>  6) & 0x1F));
		write(0x80 | ((c >>  0) & 0x3F));
		//written += 1;
	    }
	}
	//written += strlen + 2;
    }





}
