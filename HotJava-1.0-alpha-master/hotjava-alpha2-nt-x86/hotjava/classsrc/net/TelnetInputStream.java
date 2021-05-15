/*
 * @(#)TelnetInputStream.java	1.10 95/01/31 Jonathan Payne
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

import java.io.*;

package net;

/**
 * This class provides input and output streams for telnet clients.
 * This class overrides read to do CRLF processing as specified in
 * RFC 854. The class assumes it is running on a system where lines
 * are terminated with a single newline <LF> character.
 *
 * This is the relevant section of RFC 824 regarding CRLF processing:
 *
 * <pre>
 * The sequence "CR LF", as defined, will cause the NVT to be
 * positioned at the left margin of the next print line (as would,
 * for example, the sequence "LF CR").  However, many systems and
 * terminals do not treat CR and LF independently, and will have to
 * go to some effort to simulate their effect.  (For example, some
 * terminals do not have a CR independent of the LF, but on such
 * terminals it may be possible to simulate a CR by backspacing.)
 * Therefore, the sequence "CR LF" must be treated as a single "new
 * line" character and used whenever their combined action is
 * intended; the sequence "CR NUL" must be used where a carriage
 * return alone is actually desired; and the CR character must be
 * avoided in other contexts.  This rule gives assurance to systems
 * which must decide whether to perform a "new line" function or a
 * multiple-backspace that the TELNET stream contains a character
 * following a CR that will allow a rational decision.
 *
 *    Note that "CR LF" or "CR NUL" is required in both directions
 *    (in the default ASCII mode), to preserve the symmetry of the
 *    NVT model.  Even though it may be known in some situations
 *    (e.g., with remote echo and suppress go ahead options in
 *    effect) that characters are not being sent to an actual
 *    printer, nonetheless, for the sake of consistency, the protocol
 *    requires that a NUL be inserted following a CR not followed by
 *    a LF in the data stream.  The converse of this is that a NUL
 *    received in the data stream after a CR (in the absence of
 *    options negotiations which explicitly specify otherwise) should
 *    be stripped out prior to applying the NVT to local character
 *    set mapping.
 * </pre>
 *
 * @version 1.10, 31 Jan 1995
 * @author	Jonathan Payne
 */

public class TelnetInputStream extends FilterInputStream {
    /** If stickyCRLF is true, then we're a machine, like an IBM PC,
	where a Newline is a CR followed by LF.  On UNIX, this is false
	because Newline is represented with just a LF character. */
    boolean	    stickyCRLF = false;
    boolean	    seenCR = false;

    public boolean  binaryMode = false;

    public TelnetInputStream(InputStream fd, boolean binary) {
	super(fd);
	binaryMode = binary;
    }

    public void setStickyCRLF(boolean on) {
	stickyCRLF = on;
    }

    public int read() {
	if (binaryMode)
	    return super.read();

	int c;

	/* If last time we determined we saw a CRLF pair, and we're
	   not turning that into just a Newline (that is, we're
	   stickyCRLF), then return the LF part of that sticky
	   pair now. */

	if (seenCR) {
	    seenCR = false;
	    return '\n';
	}

	if ((c = super.read()) == '\r') {    /* CR */
	    switch (c = super.read()) {
	    default:
	    case -1:			    /* this is an error */
		throw new TelnetProtocolException("misplaced CR in input");

	    case 0:			    /* NUL - treat CR as CR */
		return '\r';

	    case '\n':			    /* CRLF - treat as NL */
		if (stickyCRLF) {
		    seenCR = true;
		    return '\r';
		} else {
		    return '\n';
		}
	    }
	}
	return c;
    }

    /** read into a byte array */
    public int read(byte bytes[]) {
	return read(bytes, 0, bytes.length);
    }

    /** 
     * Read into a byte array at offset <i>off</i> for length <i>length</i>
     * bytes.
     */
    public int read(byte bytes[], int off, int length) {
	if (binaryMode)
	    return super.read(bytes, off, length);

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
}

