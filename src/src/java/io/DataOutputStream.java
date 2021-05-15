/*
 * @(#)DataOutputStream.java	1.20 95/12/18 Arthur van Hoff
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
 * This class lets you write primitive Java data types
 * to a stream in a portable way. Primitive data types are well
 * understood types with associated operations.  For example, an 
 * Integer is considered to be a good primitive data type.
 *
 * The data can be converted back using a DataInputStream.
 */

public
class DataOutputStream extends FilterOutputStream implements DataOutput {
    /**
     * The number of bytes written so far.
     */
    protected int written;

    /**
     * Creates a new DataOutputStream.
     * @param out	the output stream
     */
    public DataOutputStream(OutputStream out) {
	super(out);
    }

    /**
     * Writes a byte. Will block until the byte is actually
     * written.
     * @param b the byte to be written
     * @exception IOException If an I/O error has occurred.
     */
    public synchronized void write(int b) throws IOException {
	out.write(b);
	written++;
    }

    /**
     * Writes a sub array of bytes.  
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    public synchronized void write(byte b[], int off, int len)
	throws IOException
    {
	out.write(b, off, len);
	written += len;
    }

    /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     * @exception IOException If an I/O error has occurred.
     */
    public void flush() throws IOException {
	out.flush();
    }

    /**
     * Writes a boolean.
     * @param v the boolean to be written
     */
    public final void writeBoolean(boolean v) throws IOException {
	out.write(v ? 1 : 0);
	written++;
    }

    /**
     * Writes an 8 bit byte.
     * @param v the byte value to be written
     */
    public final void writeByte(int v) throws IOException {
	out.write(v);
	written++;
    }

    /**
     * Writes a 16 bit short.
     * @param v the short value to be written
     */
    public final void writeShort(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 8) & 0xFF);
	out.write((v >>> 0) & 0xFF);
	written += 2;
    }

    /**
     * Writes a 16 bit char.
     * @param v the char value to be written
     */
    public final void writeChar(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 8) & 0xFF);
	out.write((v >>> 0) & 0xFF);
	written += 2;
    }

    /**
     * Writes a 32 bit int.
     * @param v the integer value to be written
     */
    public final void writeInt(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 24) & 0xFF);
	out.write((v >>> 16) & 0xFF);
	out.write((v >>>  8) & 0xFF);
	out.write((v >>>  0) & 0xFF);
	written += 4;
    }

    /**
     * Writes a 64 bit long.
     * @param v the long value to be written
     */
    public final void writeLong(long v) throws IOException {
	OutputStream out = this.out;
	out.write((int)(v >>> 56) & 0xFF);
	out.write((int)(v >>> 48) & 0xFF);
	out.write((int)(v >>> 40) & 0xFF);
	out.write((int)(v >>> 32) & 0xFF);
	out.write((int)(v >>> 24) & 0xFF);
	out.write((int)(v >>> 16) & 0xFF);
	out.write((int)(v >>>  8) & 0xFF);
	out.write((int)(v >>>  0) & 0xFF);
	written += 8;
    }

    /**
     * Writes a 32 bit float.
     * @param v the float value to be written
     */
    public final void writeFloat(float v) throws IOException {
	writeInt(Float.floatToIntBits(v));
    }

    /**
     * Writes a 64 bit double.
     * @param v the double value to be written
     */
    public final void writeDouble(double v) throws IOException {
	writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes a String as a sequence of bytes.
     * @param s the String of bytes to be written
     */
    public final void writeBytes(String s) throws IOException {
	OutputStream out = this.out;
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    out.write((byte)s.charAt(i));
	}
	written += len;
    }

    /**
     * Writes a String as a sequence of chars.
     * @param s the String of chars to be written
     */
    public final void writeChars(String s) throws IOException {
	OutputStream out = this.out;
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    int v = s.charAt(i);
	    out.write((v >>> 8) & 0xFF);
	    out.write((v >>> 0) & 0xFF);
	}
	written += len * 2;
    }

    /**
     * Writes a String in UTF format.
     * @param str the String in UTF format
     */
    public final void writeUTF(String str) throws IOException {
	OutputStream out = this.out;
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

	out.write((utflen >>> 8) & 0xFF);
	out.write((utflen >>> 0) & 0xFF);
	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		out.write(c);
	    } else if (c > 0x07FF) {
		out.write(0xE0 | ((c >> 12) & 0x0F));
		out.write(0x80 | ((c >>  6) & 0x3F));
		out.write(0x80 | ((c >>  0) & 0x3F));
		written += 2;
	    } else {
		out.write(0xC0 | ((c >>  6) & 0x1F));
		out.write(0x80 | ((c >>  0) & 0x3F));
		written += 1;
	    }
	}
	written += strlen + 2;
    }

    /**
     * Returns the number of bytes written.
     * @return	the number of bytes written thus far.
     */
    public final int size() {
	return written;
    }
}
