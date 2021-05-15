/*
 * @(#)Font.java	1.19 95/02/23 Sami Shaio
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

/** A font object. Use FontTable to create new fonts.
 * @see FontTable
 * @version 1.19 23 Feb 1995
 * @author Sami Shaio
 */
public class Font {
    private int pData;
    private WServer wServer;

    /* Constants to be used for styles. Can be added together to mix
       constants. */

    public static final int PLAIN	= 0;
    public static final int BOLD	= 1;
    public static final int ITALIC	= 2;
    public static final int UNDERLINE	= 4;

    public int widths[] = new int[256];

    public int		ascent;
    public int		descent;
    public int		height;

    /**
     * The actual height of the font. It may be different than the
     * height field because of font substitutions.
     */
    public int		actualHeight;

    /** The family name of this font. */
    public String	family;

    /** The style of the font. This is the sum of the
     * constants PLAIN, BOLD, ITALIC, and UNDERLINE.
     */
    public int		style;

    /**
     * Use FontTable.getFont to create a font.
     */
    Font(WServer ws, String mappedName, String name, int style, int height) {
	wServer = ws;
	family = name;
	this.style = style;
	this.height = height;
	actualHeight = height;
	ws.fontCreate(this, mappedName, style, height);
    }

    /** Convert this object to a string representation. */
    public String toString() {
	String	strStyle = "";

	if (style == Font.PLAIN) {
	    strStyle = "plain";
	} else {
	    if ((style & Font.BOLD) == 1) {
		strStyle += "bold";
	    }
	    if ((style & Font.ITALIC) == 1) {
		strStyle += "italic";
	    }
	    if ((style & Font.UNDERLINE) == 1) {
		strStyle += "underline";
	    }		
	}
	
	return getClass().getName() + "[family=" + family + ", style=" +
	    strStyle + ", height=" + height + "]";
    }

    
    /** Return the width of the specified character in this Font. */
    public int charWidth(int ch) {
	return widths[ch];
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

    /** Dispose of this font. The use of the font after calling
     * dispose is undefined.
     */
    public void dispose() {
	wServer.fontDispose(this);
    }
}
