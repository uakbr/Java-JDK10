/*
 * @(#)WSFontMetrics.java	1.1 95/04/10 Jim Graham
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
package awt;

import java.lang.*;
import awt.*;

/** A font metrics object for a WServer font.
 * @version 1.1 10 Apr 1995
 * @author Jim Graham
 */
public class WSFontMetrics extends FontMetrics {
    private WServer wServer;
    private Window drawSurface;

    /**
     * Calculate the metrics from the given WServer and font.
     */
    WSFontMetrics(Window w, Font f) {
	super(f);
	wServer = w.wServer;
	drawSurface = w;
	wServer.loadFontMetrics(w, this);
    }

    /** Return the width of the specified character in this Font. */
    public int charWidth(int ch) {
	if (widths != null)
	    return widths[ch];
	else {
	    char data[] = new char[1];

	    data[0] = (char) ch;
	    return wServer.fontCharsWidth(this, data, 0, 1);
	}
    }

    /** Return the width of the specified string in this Font. */
    public int stringWidth(String s) {
	return wServer.fontStringWidth(this, s);
    }

    /** Return the width of the specified char[] in this Font. */
    public int charsWidth(char data[], int off, int len) {
	return wServer.fontCharsWidth(this, data, off, len);
    }

    /** Return the width of the specified byte[] in this Font. */
    public int bytesWidth(byte data[], int off, int len) {
	return wServer.fontBytesWidth(this, data, off, len);
    }
}
