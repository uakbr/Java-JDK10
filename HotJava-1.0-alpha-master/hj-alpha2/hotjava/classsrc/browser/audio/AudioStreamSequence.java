/*
 * @(#)AudioStreamSequence.java	1.9 95/03/14 Arthur van Hoff
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

package browser.audio;

import java.io.InputStream;
import java.util.Enumeration;

/**
 * Convert a sequence of input streams into a single InputStream.
 * This class can be used to play two audio clips in sequence.<p>
 * For example:
 * <pre>
 *	Vector v = new Vector();
 *	v.addElement(audiostream1);
 *	v.addElement(audiostream2);
 *	AudioStreamSequence audiostream = new AudioStreamSequence(v.elements());
 *	AudioPlayer.player.start(audiostream);
 * </pre>
 * @see AudioPlayer
 * @author Arthur van Hoff
 * @version 	1.9, 14 Mar 1995
 */
public
class AudioStreamSequence extends InputStream {
    Enumeration e;
    InputStream in;
    
    /**
     * Create an AudioStreamSequence given an
     * enumeration of streams.
     */
    public AudioStreamSequence(Enumeration e) {
	this.e = e;
	in = e.hasMoreElements() ? (InputStream)e.nextElement() : null;
    }

    /**
     * Read, when reaching an EOF, flip to the next stream.
     */
    public int read() {
	if (in == null) {
	    return -1;
	}
	int c = in.read();
	if (c == -1) {
	    in.close();
	    in = null;
	    while (e.hasMoreElements() && (in == null)) {
		in = (InputStream)e.nextElement();
	    }
	    return read();
	}
	return c;
    }

    /**
     * Read, when reaching an EOF, flip to the next stream.
     */
    public int read(byte buf[], int pos, int len) {
	if (in == null) {
	    return -1;
	}
	int n = in.read(buf, pos, len);
	if (n < len) {
	    if (n < 0) {
		n = 0;
	    }
	    in.close();
	    in = e.hasMoreElements() ? (InputStream)e.nextElement() : null;
	    int m = read(buf, pos + n, len - n);
	    return (m > 0) ? n + m : n;
	}
	return n;
    }
}
