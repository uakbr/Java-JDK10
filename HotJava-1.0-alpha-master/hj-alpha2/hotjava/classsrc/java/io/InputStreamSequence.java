/*
 * Copyright (c) 1994 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)InputStreamSequence.java	1.4 95/01/31 94/10/25
 *
 * November 1994, Arthur van Hoff
 */

package java.io;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Converts a sequence of input streams into an InputStream
 */
public
class InputStreamSequence extends InputStream {
    Enumeration e;
    InputStream in;
    
    /**
     * Constructor
     */
    public InputStreamSequence(Enumeration e) {
	this.e = e;
	nextStream();
    }

    public InputStreamSequence(InputStream s1, InputStream s2) {
	Vector	v = new Vector(2);

	v.addElement(s1);
	v.addElement(s2);
	e = v.elements();
	nextStream();
    }

    final void nextStream() {
	if (in != null) {
	    in.close();
	}
	in = e.hasMoreElements() ? (InputStream) e.nextElement() : null;
    }

    /**
     * Reads, upon reaching an EOF, flips to the next stream.
     */
    public int read() {
	if (in == null) {
	    return -1;
	}
	int c = in.read();
	if (c == -1) {
	    nextStream();
	    return read();
	}
	return c;
    }

    /**
     * Reads, upon reaching an EOF, flips to the next stream.
     */
    public int read(byte buf[], int pos, int len) {
	if (in == null) {
	    return -1;
	}
	int n = in.read(buf, pos, len);
	if (n <= 0) {
	    nextStream();
	    return read(buf, pos + n, len - n);
	}
	return n;
    }

    public void close() {
	do {
	    nextStream();
	} while (in != null);
    }
}
