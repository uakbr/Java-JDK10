/*
 * @(#)SequenceInputStream.java	1.10 95/11/13 Arthur van Hoff
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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Converts a sequence of input streams into an InputStream.
 * 
 * @author   Author van Hoff
 * @version  1.10, 11/13/95
 */
public
class SequenceInputStream extends InputStream {
    Enumeration e;
    InputStream in;
    
    /**
     * Constructs a new SequenceInputStream initialized to the 
     * specified list.
     * @param e the list
     */
    public SequenceInputStream(Enumeration e) {
	this.e = e;
	try {
	    nextStream();
	} catch (IOException ex) {
	    // This should never happen
	    throw new Error("panic");
	}
    }
  
    /**
     * Constructs a new SequenceInputStream initialized to the two
     * specified input streams.
     * @param s1 the first input stream
     * @param s2 the second input stream
     */
    public SequenceInputStream(InputStream s1, InputStream s2) {
	Vector	v = new Vector(2);

	v.addElement(s1);
	v.addElement(s2);
	e = v.elements();
	try {
	    nextStream();
	} catch (IOException ex) {
	    // This should never happen
	    throw new Error("panic");
	}
    }
   
    /**
     *  Continues reading in the next stream if an EOF is reached.
     */
    final void nextStream() throws IOException {
	if (in != null) {
	    in.close();
	}
	in = e.hasMoreElements() ? (InputStream) e.nextElement() : null;
    }

    /**
     * Reads a stream, and upon reaching an EOF, flips to the next 
     * stream.
     */
    public int read() throws IOException {
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
     * Reads data into an array of bytes, and upon reaching an EOF, 
     * flips to the next stream.
     * @param buf the buffer into which the data is read
     * @param pos the start position of the data
     * @param len the maximum number of bytes read
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte buf[], int pos, int len) throws IOException {
	if (in == null) {
	    return -1;
	}
	int n = in.read(buf, pos, len);
	if (n <= 0) {
	    nextStream();
	    return read(buf, pos, len);
	}
	return n;
    }

    /**
     * Closes the input stream; flipping to the next stream,
     * if an EOF is reached.   This method must be called to release
     * any resources associated with the stream.
     * @exception IOException If an I/O error has occurred.
     */
    public void close() throws IOException {
	do {
	    nextStream();
	} while (in != null);
    }
}
