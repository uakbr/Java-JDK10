/*
 * @(#)TelnetOutputStream.java	1.7 95/01/31 Jonathan Payne
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
 * This class overrides write to do CRLF processing as specified in
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
 * @version	1.7, 31 Jan 1995
 * @author	Jonathan Payne
 */

public class TelnetOutputStream extends BufferedOutputStream {
    boolean	    stickyCRLF = false;
    boolean	    seenCR = false;

    public boolean  binaryMode = false;

    public TelnetOutputStream(FileOutputStream fd, boolean binary) {
	super(fd);
	binaryMode = binary;
    }

    /**
     * Writes the int to the stream and does CR LF processing if necessary.
     */
    public void write(int c) {
	if (binaryMode)
	    super.write(c);
	else {
	    if (seenCR) {
		if (c != '\n')
		    super.write(0);		
	    } else if (c == '\r') {
		if (stickyCRLF)
		    seenCR = true;
		else {
		    super.write('\r');
		    c = 0;
		}
	    }
	    super.write(c);
	}
    }

    /**
     * Write the bytes at offset <i>off</i> in buffer <i>bytes</i> for
     * <i>length</i> bytes.
     */
    public void write(byte bytes[], int off, int length) {
	if (binaryMode) {
	    super.write(bytes, off, length);
	    return;
	}

	while (--length >= 0) {
	    write(bytes[off++]);
	}
    }
}
