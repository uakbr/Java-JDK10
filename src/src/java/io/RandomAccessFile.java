/*
 * @(#)RandomAccessFile.java	1.27 95/11/13 David Brown
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
 * Random access files can be constructed from file descriptors, file 
 * names, or file objects.  This class provides a sense of security
 * by offering methods that allow specified mode accesses of 
 * read-only or read-write to files.
 */
public
class RandomAccessFile implements DataOutput, DataInput {
    private FileDescriptor fd;

    /**
     * Creates a RandomAccessFile with the specified system dependent 
     * file name and the specified mode.
     * Mode "r" is for read-only and mode "rw" is for read+write.
     * @param name the system dependent file name
     * @param mode the access mode
     * @exception IOException If an I/O error has occurred.
     */
    public RandomAccessFile(String name, String mode) throws IOException {
	boolean rw = mode.equals("rw");
	if (!rw && !mode.equals("r"))
		throw new IllegalArgumentException("mode must be r or rw");
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(name);
	    if (rw) {
		security.checkWrite(name);
	    }
	}
	fd = new FileDescriptor();
	open(name, rw);
    }
    
    /**
     * Creates a RandomAccessFile from a specified File object
     * and mode ("r" or "rw").     
     * @param file the file object
     * @param mode the access mode
     */
    public RandomAccessFile(File file, String mode) throws IOException {
	this(file.getPath(), mode);
    }

    /**
     * Returns the opaque file descriptor object.
     * @return the file descriptor.
     */
    public final FileDescriptor getFD() throws IOException {
	if (fd != null) return fd;
	throw new IOException();
    }

    /**
     * Opens a file and returns the file descriptor.  The file is 
     * opened in read-write mode if writeable is true, else 
     * the file is opened as read-only.
     * @param name the name of the file
     * @param writeable the boolean indicating whether file is 
     * writeable or not.
     */
    private native void open(String name, boolean writeable) throws IOException;

    // 'Read' primitives
    
    /**
     * Reads a byte of data. This method will block if no input is
     * available.
     * @return the byte read, or -1 if the end of the
     *          stream is reached. 
     * @exception IOException If an I/O error has occurred.
     */
    public native int read() throws IOException;

    /**
     * Reads a sub array as a sequence of bytes. 
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native int readBytes(byte b[], int off, int len) throws IOException;
 
    /**
     * Reads a sub array as a sequence of bytes. 
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[], int off, int len) throws IOException {
	return readBytes(b, off, len);
    }

    /**
     * Reads data into an array of bytes.  This method blocks
     * until some input is available.
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.    
     */
    public int read(byte b[]) throws IOException {
	return readBytes(b, 0, b.length);
    }
   
    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final void readFully(byte b[]) throws IOException {
	readFully(b, 0, b.length);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
	int n = 0;
	while (n < len) {
	    int count = this.read(b, off + n, len - n);
	    if (count < 0)
		throw new EOFException();
	    n += count;
	}
    }


    public int skipBytes(int n) throws IOException {
        seek(getFilePointer() + n);
        return n;
    }

    // 'Write' primitives

    /**
     * Writes a byte of data. This method will block until the byte
     * is actually written. 
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
     * @param b the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[]) throws IOException {
	writeBytes(b, 0, b.length); 
    }

    /**
     * Wrotes a sub array of bytes.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred. 
     */
    public void write(byte b[], int off, int len) throws IOException {
	writeBytes(b, off, len);
    }

    // 'Random access' stuff


    /**
     * Returns the current location of the file pointer.
     */
    public native long getFilePointer() throws IOException;

    /**
     * Sets the file pointer to the specified absolute position.
     * @param pos the absolute position
     */
    public native void seek(long pos) throws IOException;

    /**
     * Returns the length of the file.
     */
    public native long length() throws IOException;

    /**
     * Closes the file. 
     * @exception IOException If an I/O error has occurred.
     */
    public native void close() throws IOException;

    //
    //  Some "reading/writing Java data types" methods stolen from
    //  DataInputStream and DataOutputStream.
    //

    /**
     * Reads a boolean.
     */
    public final boolean readBoolean() throws IOException {
	int ch = this.read();
	if (ch < 0)
	    throw new EOFException();
	return (ch != 0);
    }

    /**
     * Reads a byte.
     */
    public final byte readByte() throws IOException {
	int ch = this.read();
	if (ch < 0)
	    throw new EOFException();
	return (byte)(ch);
    }


    /**
     * Reads an unsigned 8 bit byte.
     * @return the 8 bit byte read.
     */
    public final int readUnsignedByte() throws IOException {
	int ch = this.read();
	if (ch < 0)
	    throw new EOFException();
	return ch;
    }


    /**
     * Reads 16 bit short.
     * @return the read 16 bit short.
     */
    public final short readShort() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (short)((ch1 << 8) + (ch2 << 0));
    }


    /**
     * Reads 16 bit short.
     * @return the read 16 bit short.
     */
    public final int readUnsignedShort() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (ch1 << 8) + (ch2 << 0);
    }


    /**
     * Reads a 16 bit char.
     * @return the read 16 bit char. 
     */
    public final char readChar() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (char)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads a 32 bit int.
     * @return the read 32 bit integer.
     */
    public final int readInt() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	int ch3 = this.read();
	int ch4 = this.read();
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	     throw new EOFException();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * Reads a 64 bit long.
     * @return the read 64 bit long.
     */
    public final long readLong() throws IOException {
	return (this.readInt() << 32L) + (this.readInt() & 0xFFFFFFFFL);
    }

    /**
     * Reads a 32 bit float.
     * @return the read 32 bit float.
     */
    public final float readFloat() throws IOException {
	return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a 64 bit double.
     * @return the read 64 bit double.
     */
    public final double readDouble() throws IOException {
	return Double.longBitsToDouble(readLong());
    }

    /**
     * Reads a line terminated by a '\n' or EOF.
     */
    public final String readLine() throws IOException {
	StringBuffer input = new StringBuffer();
	int c;

	while (((c = read()) != -1) && (c != '\n')) {
	    input.append((char)c);
	}
	if ((c == -1) && (input.length() == 0)) {
	    return null;
	}
	return input.toString();
    }

    /**
     * Reads a UTF formatted String.
     */
    public final String readUTF() throws IOException {
	return DataInputStream.readUTF(this);
    }

    /**
     * Writes a boolean.
     * @param v the boolean value
     */
    public final void writeBoolean(boolean v) throws IOException {
	write(v ? 1 : 0);
	//written++;
    }

    /**
     * Writes a byte.
     * @param v the byte
     */
    public final void writeByte(int v) throws IOException {
	write(v);
	//written++;
    }

    /**
     * Writes a short.
     * @param v the short
     */
    public final void writeShort(int v) throws IOException {
	write((v >>> 8) & 0xFF);
	write((v >>> 0) & 0xFF);
	//written += 2;
    }

    /**
     * Writes a character.
     * @param v the char
     */
    public final void writeChar(int v) throws IOException {
	write((v >>> 8) & 0xFF);
	write((v >>> 0) & 0xFF);
	//written += 2;
    }

    /**
     * Writes an integer.
     * @param v the integer
     */
    public final void writeInt(int v) throws IOException {
	write((v >>> 24) & 0xFF);
	write((v >>> 16) & 0xFF);
	write((v >>>  8) & 0xFF);
	write((v >>>  0) & 0xFF);
	//written += 4;
    }

    /**
     * Writes a long.
     * @param v the long
     */
    public final void writeLong(long v) throws IOException {
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

    /*
     * Writes a 32 bit float.
     * @param v the float value to be written
     */
    public final void writeFloat(float v) throws IOException {
	writeInt(Float.floatToIntBits(v));
    }


    /*
     * Writes a 64 bit double.
     * @param v the double value to be written
     */
    public final void writeDouble(double v) throws IOException {
	writeLong(Double.doubleToLongBits(v));
    }


    /**
     * Writes a String as a sequence of bytes.
     * @param s the String
     */
    public final void writeBytes(String s) throws IOException {
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    write((byte)s.charAt(i));
	}
	//written += len;
    }

    /**
     * Writes a String as a sequence of chars.
     * @param s the String
     */
    public final void writeChars(String s) throws IOException {
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    int v = s.charAt(i);
	    write((v >>> 8) & 0xFF);
	    write((v >>> 0) & 0xFF);
	}
	//written += len * 2;
    }

    /**
     * Writes a String in UTF format.
     * @param str the String
     */
    public final void writeUTF(String str) throws IOException {
	int strlen = str.length();
	int utflen = 0;

	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
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
	    } else if (c > 0x07FF) {
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
