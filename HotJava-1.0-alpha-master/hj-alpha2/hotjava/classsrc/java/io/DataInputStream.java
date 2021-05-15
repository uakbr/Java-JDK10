/*
 * @(#)DataInputStream.java	1.16 95/01/31 Arthur van Hoff
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
 * A data input stream that lets you read primitive Java data types
 * from a stream in a portable way.
 *
 * @see DataOutputStream
 * @version 	1.16, 31 Jan 1995
 * @author	Arthur van Hoff
 */
public
class DataInputStream extends FilterInputStream {
    /**
     * Create a new DataInputStream.
     * @param in 	the input stream
     */
    public DataInputStream(InputStream in) {
	super(in);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public final int readBytes(byte b[]) {
	return readBytes(b, 0, b.length);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException i/o error occurred
     */
    public final int readBytes(byte b[], int off, int len) {
	InputStream in = this.in;
	int i, n = 0;
	while (n < len) {
	    if ((i = in.read(b, off + n, len - n)) < 0) {
		return (n > 0) ? n : -1;
	    }
	    n += i;
	}
	return n;
    }

    /**
     * Skips bytes, block until all bytes are skipped.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     * @exception IOException i/o error occurred
     */
    public final int skipBytes(int n) {
	InputStream in = this.in;
	for (int i = 0 ; i < n ; i += in.skip(n - i));
	return n;
    }

    /**
     * Reads a boolean.
     */
    public final boolean readBoolean() {
	return in.read() != 0;
    }

    /**
     * Reads an 8 bit byte.
     */
    public final byte readByte() {
	return (byte)in.read();
    }

    /**
     * Reads 16 bit short.
     */
    public final short readShort() {
	InputStream in = this.in;
	return (short)(((in.read() << 8) & (0xFF << 8)) |
		       ((in.read() << 0) & (0xFF << 0)));
    }

    /**
     * Reads a 16 bit char.
     */
    public final char readChar() {
	InputStream in = this.in;
	return (char)(((in.read() << 8) & (0xFF << 8)) |
		      ((in.read() << 0) & (0xFF << 0)));

    }

    /**
     * Reads a 32 bit int.
     */
    public final int readInt() {
	InputStream in = this.in;
	return ((in.read() << 24) & (0xFF << 24)) |
	       ((in.read() << 16) & (0xFF << 16)) |
	       ((in.read() <<  8) & (0xFF <<  8)) |
	       ((in.read() <<  0) & (0xFF <<  0));
    }

    /**
     * Reads a 64 bit long.
     */
    public final long readLong() {
	InputStream in = this.in;
	return (((long)in.read() << 56) & ((long)0xFF << 56)) |
	       (((long)in.read() << 48) & ((long)0xFF << 48)) |
	       (((long)in.read() << 40) & ((long)0xFF << 40)) |
	       (((long)in.read() << 32) & ((long)0xFF << 32)) |
	       (((long)in.read() << 24) & ((long)0xFF << 24)) |
	       (((long)in.read() << 16) & ((long)0xFF << 16)) | 
	       (((long)in.read() <<  8) & ((long)0xFF <<  8)) |
	       (((long)in.read() <<  0) & ((long)0xFF <<  0));
    }

    /**
     * Reads a 32 bit float.
     */
    public final float readFloat() {
	return int2float(readInt());
    }

    /**
     * Reads a 64 bit double.
     */
    public final double readDouble() {
	return long2double(readLong());
    }

    /**
     * Reads a line terminated by a '\n' or EOF.
     */
    public final String readLine() {
	InputStream in = this.in;
	StringBuffer input = new StringBuffer();
	int c;

	while (((c = in.read()) != -1) && (c != '\n')) {
	    input.appendChar((char)c);
	}
	if ((c == -1) && (input.length() == 0)) {
	    return null;
	}
	return input.toString();
    }

    /**
     * Reads a UTF format string.
     */
    public final String readUTF() {
	InputStream in = this.in;
	int utflen = ((in.read() << 8) & 0xFF00) |
	             ((in.read() << 0) & 0x00FF);
	char str[] = new char[utflen];
	int strlen = 0;


	for (int i = 0 ; i < utflen ;) {
	    int c = in.read();
	    if ((c & 0x80) == 0) {
		str[strlen++] = (char)c;
		i++;
	    } else if ((c & 0xE0) == 0xC0) {
		str[strlen++] = (char)(((c & 0x1F) << 6) | (in.read() & 0x3F));
		i += 2;
	    } else {
		str[strlen++] = (char)(((c & 0x0F) << 12) |
				       ((in.read() & 0x3F) << 6) |
				       (in.read() & 0x3F));
		i += 3;
	    } 
	}
	return new String(str, 0, strlen);
    }

    /*
     * Conversions
     */
    private native float int2float(int v);
    private native double long2double(long v);
}
