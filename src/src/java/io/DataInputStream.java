/*
 * @(#)DataInputStream.java	1.28 95/12/18 Arthur van Hoff
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
 * from a stream in a portable way.  Primitive data types are well
 * understood types with associated operations.  For example,
 * Integers are considered primitive data types. 
 *
 * @see DataOutputStream
 * @version 	1.28, 12/18/95
 * @author	Arthur van Hoff
 */
public
class DataInputStream extends FilterInputStream implements DataInput {
    /**
     * Creates a new DataInputStream.
     * @param in 	the input stream
     */
    public DataInputStream(InputStream in) {
	super(in);
    }

    /**
     * Reads data into an array of bytes.
     * This method blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final int read(byte b[]) throws IOException {
	return in.read(b, 0, b.length);
    }

    /**
     * Reads data into an array of bytes.
     * This method blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final int read(byte b[], int off, int len) throws IOException {
	return in.read(b, off, len);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @exception IOException If an I/O error has occurred.
     * @exception EOFException If EOF reached before all bytes are read.
     */
    public final void readFully(byte b[]) throws IOException {
	readFully(b, 0, b.length);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @exception IOException If an I/O error has occurred.
     * @exception EOFException If EOF reached before all bytes are read.
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
	InputStream in = this.in;
	int n = 0;
	while (n < len) {
	    int count = in.read(b, off + n, len - n);
	    if (count < 0)
		throw new EOFException();
	    n += count;
	}
    }

    /**
     * Skips bytes, blocks until all bytes are skipped.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public final int skipBytes(int n) throws IOException {
	InputStream in = this.in;
	for (int i = 0 ; i < n ; i += (int)in.skip(n - i));
	return n;
    }

    /**
     * Reads a boolean.
     * @return the boolean read.
     */
    public final boolean readBoolean() throws IOException {
	int ch = in.read();
	if (ch < 0)
	    throw new EOFException();
	return (ch != 0);
    }

    /**
     * Reads an 8 bit byte.
     * @return the 8 bit byte read.
     */
    public final byte readByte() throws IOException {
	int ch = in.read();
	if (ch < 0)
	    throw new EOFException();
	return (byte)(ch);
    }

    /**
     * Reads an unsigned 8 bit byte.
     * @return the 8 bit byte read.
     */
    public final int readUnsignedByte() throws IOException {
	int ch = in.read();
	if (ch < 0)
	    throw new EOFException();
	return ch;
    }


    /**
     * Reads a 16 bit short.
     * @return the 16 bit short read.
     */
    public final short readShort() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (short)((ch1 << 8) + (ch2 << 0));
    }


    /**
     * Reads 16 bit short.
     * @return the 16 bit short read.
     */
    public final int readUnsignedShort() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (ch1 << 8) + (ch2 << 0);
    }


    /**
     * Reads a 16 bit char.
     * @return the read 16 bit char. 
     */
    public final char readChar() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (char)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads a 32 bit int.
     * @return the 32 bit integer read.
     */
    public final int readInt() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	int ch3 = in.read();
	int ch4 = in.read();
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	     throw new EOFException();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * Reads a 64 bit long.
     * @return the 64 bit long read.
     */
    public final long readLong() throws IOException {
	InputStream in = this.in;
	return (readInt() << 32L) + (readInt() & 0xFFFFFFFFL);
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
     * @return the 64 bit double read.
     */
    public final double readDouble() throws IOException {
	return Double.longBitsToDouble(readLong());
    }

    private char lineBuffer[];

    /**
     * Reads in a line that has been terminated by a \n, \r, 
     * \r\n or EOF.
     * @return a String copy of the line.
     */
    public final String readLine() throws IOException {
	InputStream in = this.in;
	char buf[] = lineBuffer;

	if (buf == null) {
	    buf = lineBuffer = new char[128];
	}

	int room = buf.length;
	int offset = 0;
	int c;

loop:	while (true) {
	    switch (c = in.read()) {
	      case -1: 
	      case '\n':
		break loop;

	      case '\r':
		int c2 = in.read();
		if (c2 != '\n') {
		    if (!(in instanceof PushbackInputStream)) {
			in = this.in = new PushbackInputStream(in);
		    }
		    ((PushbackInputStream)in).unread(c2);
		}
		break loop;

	      default:
		if (--room < 0) {
		    buf = new char[offset + 128];
		    room = buf.length - offset - 1;
		    System.arraycopy(lineBuffer, 0, buf, 0, offset);
		    lineBuffer = buf;
		}
		buf[offset++] = (char) c;
		break;
	    }
	}
	if ((c == -1) && (offset == 0)) {
	    return null;
	}
	return String.copyValueOf(buf, 0, offset);
    }

    /**
     * Reads a UTF format String.
     * @return the String.
     */
    public final String readUTF() throws IOException {
        return readUTF(this);
    }

    /**
     * Reads a UTF format String from the given input stream.
     * @return the String.
     */
    public final static String readUTF(DataInput in) throws IOException {
        int utflen = in.readUnsignedShort();
        char str[] = new char[utflen];
	int count = 0;
	int strlen = 0;
	while (count < utflen) {
	    int c = in.readUnsignedByte();
	    int char2, char3;
	    switch (c >> 4) { 
	        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		    // 0xxxxxxx
		    count++;
		    str[strlen++] = (char)c;
		    break;
	        case 12: case 13:
		    // 110x xxxx   10xx xxxx
		    count += 2;
		    if (count > utflen) 
			throw new UTFDataFormatException();		  
		    char2 = in.readUnsignedByte();
		    if ((char2 & 0xC0) != 0x80)
			throw new UTFDataFormatException();		  
		    str[strlen++] = (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
		    break;
	        case 14:
		    // 1110 xxxx  10xx xxxx  10xx xxxx
		    count += 3;
		    if (count > utflen) 
			throw new UTFDataFormatException();		  
		    char2 = in.readUnsignedByte();
		    char3 = in.readUnsignedByte();
		    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
			throw new UTFDataFormatException();		  
		    str[strlen++] = (char)(((c & 0x0F) << 12) |
					   ((char2 & 0x3F) << 6) |
					   ((char3 & 0x3F) << 0));
	        default:
		    // 10xx xxxx,  1111 xxxx
		    throw new UTFDataFormatException();		  
		}
	}
        return new String(str, 0, strlen);
    }
}


