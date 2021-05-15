/*
 * @(#)DataOutputStream.java	1.11 95/01/31 Arthur van Hoff
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
 * to a stream in a portable way. The data can be converted
 * back using a DataInputStream.
 */

public
class DataOutputStream extends FilterOutputStream {
    /**
     * The number of bytes written so far.
     */
    protected int written;

    /**
     * Creates a new DataOutputStream
     * @param out	the output stream
     */
    public DataOutputStream(OutputStream out) {
	super(out);
    }

    /**
     * Writes a byte. Will block until the byte is actually
     * written.
     * @exception IOException i/o error occurred
     */
    public synchronized void write(int b) {
	out.write(b);
	written++;
    }

    /**
     * Writes a sub array of bytes. To be efficient it should
     * be overridden in a subclass. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException i/o error occurred
     */
    public synchronized void write(byte b[], int off, int len) {
	out.write(b, off, len);
	written += len;
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
     * Writes a boolean.
     */
    public final void writeBoolean(boolean v) {
	out.write(v ? 1 : 0);
	written++;
    }

    /**
     * Writes an 8 bit byte.
     */
    public final void writeByte(int v) {
	out.write(v);
	written++;
    }

    /**
     * Writes a 16 bit short.
     */
    public final void writeShort(int v) {
	OutputStream out = this.out;
	out.write((v >>> 8) & 0xFF);
	out.write((v >>> 0) & 0xFF);
	written += 2;
    }

    /**
     * Writes a 16 bit char.
     */
    public final void writeChar(int v) {
	OutputStream out = this.out;
	out.write((v >>> 8) & 0xFF);
	out.write((v >>> 0) & 0xFF);
	written += 2;
    }

    /**
     * Writes a 32 bit int.
     */
    public final void writeInt(int v) {
	OutputStream out = this.out;
	out.write((v >>> 24) & 0xFF);
	out.write((v >>> 16) & 0xFF);
	out.write((v >>>  8) & 0xFF);
	out.write((v >>>  0) & 0xFF);
	written += 4;
    }

    /**
     * Writes a 64 bit long.
     */
    public final void writeLong(long v) {
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
     */
    public final void writeFloat(float v) {
	writeInt(float2int(v));
    }

    /**
     * Writes a 64 bit double.
     */
    public final void writeDouble(double v) {
	writeLong(double2long(v));
    }

    /**
     * Writes a string as a sequence of bytes.
     */
    public final void writeBytes(String s) {
	OutputStream out = this.out;
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    out.write((byte)s.charAt(i));
	}
	written += len;
    }

    /**
     * Writes a string as a sequence of chars.
     */
    public final void writeChars(String s) {
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
     * Writes string in UTF format.
     */
    public final void writeUTF(String str) {
	OutputStream out = this.out;
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

	out.write((utflen >>> 8) & 0xFF);
	out.write((utflen >>> 0) & 0xFF);
	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		out.write(c);
	    } else if (c > 0x03FF) {
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
     * Returns number of bytes written.
     * @return	the number of bytes written sofar
     */
    public final int size() {
	return written;
    }

    /**
     * Aligns. This is useful when data fields have
     * to be aligned on a n byte boundary.
     * @param n	the number of bytes to align on
     */
    public final void align(int n) {
	OutputStream out = this.out;
	while ((written % n) != 0) {
	    out.write(0);
	}
    }

    /*
     * Conversions
     */
    private native int float2int(float v);
    private native long double2long(double v);
}
