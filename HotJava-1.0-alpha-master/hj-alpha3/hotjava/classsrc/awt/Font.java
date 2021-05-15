/*
 * @(#)Font.java	1.23 95/05/16 Sami Shaio
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

/** A font object.  Since the pData field could point to an arbitrary
 * amount of information needed by the AWT library, FontTable should
 * be used to create new fonts to guarantee sharing of information.
 *
 * @see FontTable
 * @see awt.Window#getFontMetrics
 * @see awt.Graphics#getFontMetrics
 * @version 1.23 16 May 1995
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

    /** The family name of this font. */
    public String	family;

    /** The style of the font. This is the sum of the
     * constants PLAIN, BOLD, ITALIC, or UNDERLINE.
     */
    public int		style;

    /** The point size of this font. */
    public int		size;

    /** The logical ascent for this font, not guaranteed to be initialized
     * until the first time the metrics for this font are fetched and
     * not guaranteed to be applicable to all output devices.  This field
     * is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int		ascent;

    /** The logical descent for this font, not guaranteed to be initialized
     * until the first time the metrics for this font are fetched and
     * not guaranteed to be applicable to all output devices.  This field
     * is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int		descent;

    /** The old field name for the point size of this font.  This field
     * was renamed to "size" to avoid confusion with the font metric of
     * the same name which provides information about how tall the font
     * actually is.  This field is deprecated.  Use size to refer to the
     * requested size of the font and use getFontMetrics to obtain
     * information about the font for layout purposes.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int		height;

    /** The actual logical height of this font after possible size
     * substitutions occur in the awt library when looking for a font
     * match.  This field is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int		actualHeight;

    /** This field is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int		widths[];

    /**
     * Use FontTable.getFont to create a font.
     */
    Font(String family, int style, int points) {
	this.family = family;
	this.style = style;
	height = size = points;
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
	    strStyle + ", size=" + size + "]";
    }

    /** Return the width of the specified character in this Font.
     * This function is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int charWidth(int ch) {
	return widths[ch];
    }

    /** Return the width of the specified string in this Font.
     * This function is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int stringWidth(String s) {
	int w = 0;
	int len = s.length();
	for (int i = 0; i < len; i++) {
	    w += widths[s.charAt(i)];
	}
	return w;
    }

    /** Return the width of the specified char[] in this Font.
     * This function is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int charsWidth(char data[], int off, int len) {
	int w = 0;
	for (int i = 0; i < len; i++) {
	    w += widths[data[i + off]];
	}
	return w;
    }

    /** Return the width of the specified byte[] in this Font.
     * This function is deprecated.  Use getFontMetrics instead.
     * @see awt.Window#getFontMetrics
     * @see awt.Graphics#getFontMetrics
     */
    public int bytesWidth(byte data[], int off, int len) {
	int w = 0;
	for (int i = 0; i < len; i++) {
	    w += widths[data[i + off]];
	}
	return w;
    }

    /** Dispose of this font. The use of the font after calling
     * dispose is undefined.
     */
    public void dispose() {
	if (wServer != null) {
	    wServer.fontDispose(this);
	}
    }

    public void finalize() {
	dispose();
    }
}
