/*
 * @(#)NntpInputStream.java	1.6 94/12/12 Jonathan Payne
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

package net.nntp;

import java.io.*;
import java.util.*;

/**
 * This class provides the input stream for the NNTP client.
 *
 * @version	1.6, 12 Dec 1994
 * @author	Jonathan Payne
 * @see		NntpClient
 */
class NntpInputStream extends FilterInputStream {
    int	    column = 0;
    boolean eofOccurred = false;

    public NntpInputStream(InputStream child) {
	super(child);
    }

    int eof() {
	eofOccurred = true;
	return -1;
    }

    /**
     * Read data from the NNTP stream.
     * @exception NntpProtocolException thrown on bad data being read
     */
    public int read() {
	int c;

	if (eofOccurred)
	    return -1;

	c = super.read();
	if (c == '.' && column == 0) {
	    c = super.read();
	    if (c == '\n')
		return eof();
	    if (c != '.')
		throw new NntpProtocolException("Expecting '.' - got " + c);
	}
	if (c == '\n')
	    column = 0;
	else
	    column += 1;
	return c;
    }

    /**
     * Fills <i>bytes</i> with data read from the stream.
     * @exception NntpProtocolException see read() above.
     */
    public int read(byte bytes[]) {
	return read(bytes, 0, bytes.length);
    }

    /**
     * Reads <i>length</i> bytes into byte array <i>bytes</i> at offset 
     * <i>off</i> with data read from the stream.
     * @exception NntpProtocolException see read() above.
     */
    public int read(byte bytes[], int off, int length) {
	int c;
	int offStart = off;

	while (--length >= 0) {
	    c = read();
	    if (c == -1)
		break;
	    bytes[off++] = (byte)c;
	}
	return (off > offStart) ? off - offStart : -1;
    }

    /** Close the input stream.  We do nothing here because we
        know we don't actually own this stream.  We're splicing
	ourselves into this stream and returning EOF after we
	have read a subset of the input. */

    public void close() {}
}

